import pika
import json
import threading
from typing import Dict, Any, Callable
from app.config import (
    RABBITMQ_HOST, RABBITMQ_PORT, RABBITMQ_USERNAME, RABBITMQ_PASSWORD,
    QUEUE_PYTHON_TITLE_REQUEST, QUEUE_PYTHON_TITLE_RESPONSE,
    QUEUE_PYTHON_MESSAGE_REQUEST, QUEUE_PYTHON_MESSAGE_RESPONSE,
    QUEUE_PYTHON_CANCEL, EXCHANGE_PYTHON
)

class RabbitMQHandler:
    def __init__(self, host=RABBITMQ_HOST, port=RABBITMQ_PORT, 
                 username=RABBITMQ_USERNAME, password=RABBITMQ_PASSWORD):
        self.connection = None
        self.channel = None
        self.host = host
        self.port = port
        self.credentials = pika.PlainCredentials(username, password)
        self.should_stop = False
        self.consumer_thread = None
        
    def connect(self):
        """Establish connection to RabbitMQ server"""
        try:
            self.connection = pika.BlockingConnection(
                pika.ConnectionParameters(
                    host=self.host,
                    port=self.port,
                    credentials=self.credentials
                )
            )
            self.channel = self.connection.channel()
            print("✅ Connected to RabbitMQ")
            return True
        except Exception as e:
            print(f"❌ Failed to connect to RabbitMQ: {str(e)}")
            return False
            
    def setup_queues(self):
        """Declare queues matching Spring Boot configuration"""
        # Define the queues as in Spring Boot config
        self.channel.queue_declare(queue=QUEUE_PYTHON_TITLE_REQUEST, durable=True)
        self.channel.queue_declare(queue=QUEUE_PYTHON_TITLE_RESPONSE, durable=True)
        self.channel.queue_declare(queue=QUEUE_PYTHON_MESSAGE_REQUEST, durable=True)
        self.channel.queue_declare(queue=QUEUE_PYTHON_MESSAGE_RESPONSE, durable=True)
        self.channel.queue_declare(queue=QUEUE_PYTHON_CANCEL, durable=True)
        
        # Declare the exchange
        self.channel.exchange_declare(
            exchange=EXCHANGE_PYTHON,
            exchange_type='direct',
            durable=False
        )
        
        # Bind queues to the exchange with appropriate routing keys
        self.channel.queue_bind(
            exchange=EXCHANGE_PYTHON,
            queue=QUEUE_PYTHON_TITLE_REQUEST,
            routing_key='generate-title'
        )
        
        self.channel.queue_bind(
            exchange=EXCHANGE_PYTHON,
            queue=QUEUE_PYTHON_MESSAGE_REQUEST,
            routing_key='message-request'
        )
        
        self.channel.queue_bind(
            exchange=EXCHANGE_PYTHON,
            queue=QUEUE_PYTHON_CANCEL,
            routing_key='cancel'
        )
        
        print("✅ RabbitMQ queues and exchanges configured")
        
    def start_consuming(self, 
                       process_callback: Callable[[Dict[str, Any]], Dict[str, Any]], 
                       cancel_callback: Callable[[Dict[str, Any]], None],
                       generate_title_callback: Callable[[Dict[str, Any]], Dict[str, Any]]):
        """Start consuming messages in a separate thread"""
        def consumer_worker():
            # Title generation request handler
            def on_title_request(ch, method, properties, body):
                try:
                    request = json.loads(body)
                    print(f"📝 Received title generation request: {request}")
                    
                    # Process the title generation request
                    response = generate_title_callback(request)
                    
                    # Send response to the title response queue
                    self.send_title_response(response)
                    
                    # Acknowledge message
                    ch.basic_ack(delivery_tag=method.delivery_tag)
                except Exception as e:
                    print(f"❌ Error processing title request: {str(e)}")
                    # Send error response for title generation
                    error_response = {
                        "sessionId": request.get("sessionId") if 'request' in locals() else "unknown",
                        "requestId": request.get("requestId") if 'request' in locals() else "unknown",
                        "title": "Error generating title",
                        "operation": "TITLE_GENERATION",
                        "status": "ERROR",
                        "error": str(e)
                    }
                    self.send_title_response(error_response)
                    ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
            
            # Message processing handler
            def on_message_request(ch, method, properties, body):
                try:
                    request = json.loads(body)
                    print(f"💬 Received message processing request: {request}")
                    
                    # Process the message
                    response = process_callback(request)
                    
                    # Send response to the message response queue
                    self.send_message_response(response)
                    
                    # Acknowledge message
                    ch.basic_ack(delivery_tag=method.delivery_tag)
                except Exception as e:
                    print(f"❌ Error processing message: {str(e)}")
                    # Send error response for message
                    error_response = {
                        "sessionId": request.get("sessionId") if 'request' in locals() else "unknown",
                        "requestId": request.get("requestId") if 'request' in locals() else "unknown",
                        "content": f"Error: {str(e)}",
                        "role": "BOT",
                        "status": "ERROR"
                    }
                    self.send_message_response(error_response)
                    ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)

            # Cancel processing handler
            def on_cancel_message(ch, method, properties, body):
                try:
                    message = json.loads(body)
                    print(f"🛑 Received cancellation request: {message}")
                    
                    # Call the callback to handle cancellation
                    cancel_callback(message)
                    
                    ch.basic_ack(delivery_tag=method.delivery_tag)
                except Exception as e:
                    print(f"❌ Error processing cancellation: {str(e)}")
                    ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
            
            # Set up consumers for each queue
            self.channel.basic_consume(
                queue=QUEUE_PYTHON_TITLE_REQUEST,
                on_message_callback=on_title_request,
                auto_ack=False
            )

            self.channel.basic_consume(
                queue=QUEUE_PYTHON_MESSAGE_REQUEST,
                on_message_callback=on_message_request,
                auto_ack=False
            )

            self.channel.basic_consume(
                queue=QUEUE_PYTHON_CANCEL,
                on_message_callback=on_cancel_message,
                auto_ack=False
            )
            
            print("🔄 Started consuming messages from RabbitMQ")
            
            # Start consuming (blocking call)
            while not self.should_stop:
                self.connection.process_data_events(time_limit=1)  # Process events with timeout
                
            print("🛑 Stopped consuming messages from RabbitMQ")
        
        # Start the consumer in a separate thread
        self.consumer_thread = threading.Thread(target=consumer_worker)
        self.consumer_thread.daemon = True
        self.consumer_thread.start()
        
    def send_message_response(self, response: Dict[str, Any]):
        """Send message response back to Spring Boot"""
        try:
            self.channel.basic_publish(
                exchange=EXCHANGE_PYTHON,
                routing_key='message-response',
                body=json.dumps(response),
                properties=pika.BasicProperties(
                    delivery_mode=2,  # make message persistent
                    content_type='application/json'
                )
            )
            content_preview = response.get('content', '')[:50]
            print(f"📤 Sent message response: {content_preview}...")
        except Exception as e:
            print(f"❌ Failed to send message response: {str(e)}")
    
    def send_title_response(self, response: Dict[str, Any]):
        """Send title response back to Spring Boot"""
        try:
            self.channel.basic_publish(
                exchange=EXCHANGE_PYTHON,
                routing_key='title-response',
                body=json.dumps(response),
                properties=pika.BasicProperties(
                    delivery_mode=2,  # make message persistent
                    content_type='application/json'
                )
            )
            title = response.get('title', '')
            print(f"📤 Sent title response: {title}")
        except Exception as e:
            print(f"❌ Failed to send title response: {str(e)}")
            
    def stop(self):
        """Stop consuming and close connection"""
        self.should_stop = True
        if self.consumer_thread:
            self.consumer_thread.join(timeout=5)
        if self.connection and self.connection.is_open:
            self.connection.close()
            print("🔒 RabbitMQ connection closed")
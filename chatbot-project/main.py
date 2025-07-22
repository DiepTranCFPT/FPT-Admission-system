import vertexai
import uvicorn
from contextlib import asynccontextmanager
from fastapi import FastAPI
from vertexai.language_models import TextEmbeddingModel
from vertexai.generative_models import GenerativeModel

from app.api import create_app
from app.services.embeddings import init_embedding_service
from app.services.chatbot import init_chatbot_service, generate_session_title, process_message, cancel_processing
from app.services.rabbitmq import RabbitMQHandler
from app.services.mongodb import mongodb_service
from app.config import PROJECT_ID, LOCATION

# Global variables
rabbit_handler = None

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    global rabbit_handler
    
    print("🚀 Starting up chatbot service...")
    
    # Initialize MongoDB connection
    if not mongodb_service.connect():
        print("❌ Failed to connect to MongoDB, shutting down...")
        return
    
    mongodb_service.create_indexes()
    
    # Initialize Vertex AI
    vertexai.init(project=PROJECT_ID, location=LOCATION)
    
    # Load models
    embedding_model = TextEmbeddingModel.from_pretrained("text-embedding-004")
    generation_model = GenerativeModel("gemini-2.0-flash")
    
    # Initialize services
    init_embedding_service(embedding_model)
    init_chatbot_service(generation_model)
    
    # Initialize and start RabbitMQ handler
    rabbit_handler = RabbitMQHandler()
    
    if rabbit_handler.connect():
        rabbit_handler.setup_queues()
        rabbit_handler.start_consuming(
            generate_title_callback=generate_session_title,
            process_callback=process_message,
            cancel_callback=cancel_processing
        )
    
    yield  # This is where the app runs
    
    # Shutdown
    if rabbit_handler:
        rabbit_handler.stop()
    
    # Close MongoDB connection
    mongodb_service.close()
    
    print("🛑 Shutting down chatbot service...")

# Create the FastAPI app
app = create_app(lifespan)

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
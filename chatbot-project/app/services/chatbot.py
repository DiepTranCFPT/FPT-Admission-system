from typing import Dict, Any, Tuple
from concurrent.futures import CancelledError
from .embeddings import retrieve_context

# Global variables
generation_model = None
active_tasks = {}  # Store active generation tasks for cancellation

def init_chatbot_service(model):
    """Initialize the chatbot with the generative model"""
    global generation_model
    generation_model = model
    return True

def answer_question(query: str) -> Tuple[str, str]:
    """Answer a question using context from embeddings"""
    if not generation_model:
        raise ValueError("Chatbot service not initialized")
    
    # Retrieve relevant context using the same logic as chatbot_service.py
    context = retrieve_context(query)
    
    # Create prompt with context using Vietnamese format like chatbot_service.py
    prompt = f"Bối cảnh:\n{context}\n\nCâu hỏi: {query}\nTrả lời bằng tiếng Việt:"
    
    # Generate response
    response = generation_model.generate_content(prompt)
    
    return response.text, context

def generate_session_title(message: Dict[str, Any]) -> Dict[str, Any]:
    """Generate a title for a chat session based on the first message"""
    session_id = message.get("sessionId")
    request_id = message.get("requestId")
    first_message = message.get("content")

    # Store task ID for potential cancellation
    task_id = f"{session_id}:{request_id}"
    
    if not generation_model:
        raise ValueError("Chatbot service not initialized")
    
    try:
        # Simpler version - just truncate the message if it's under threshold
        if len(first_message) <= 40:
            title = first_message
        else:
            # For longer messages, use AI to generate a title
            prompt = f"""
            Create a short, descriptive title (maximum 40 characters) for a conversation that starts with this message:
            
            "{first_message}"
            
            Return only the title without quotes or additional explanations.
            """
            
            # Generate title with AI
            response = generation_model.generate_content(prompt)
            title = response.text.strip()
            
            # Ensure the title is not too long
            if len(title) > 40:
                title = title[:37] + "..."
        
        # Return in the expected format

        print({
            "sessionId": session_id,
            "requestId": request_id,
            "title": title,
            "operation": "TITLE_GENERATION",
            "status": "COMPLETED"
        })
        return {
            "sessionId": session_id,
            "requestId": request_id,
            "title": title,
            "operation": "TITLE_GENERATION",
            "status": "COMPLETED"
        }
    
    except Exception as e:
        # Fallback to truncation if AI generation fails
        print(f"Error generating title: {str(e)}")
        
        return {
            "sessionId": session_id,
            "requestId": request_id,
            "title": first_message[:37] + "..." if len(first_message) > 40 else first_message,
            "operation": "TITLE_GENERATION",
            "status": "ERROR",
            "error": str(e)
        }
    finally:
        # Remove task from active tasks
        if task_id in active_tasks:
            del active_tasks[task_id]

def process_message(message: Dict[str, Any]) -> Dict[str, Any]:
    """Process a message from RabbitMQ"""
    session_id = message.get("sessionId")
    request_id = message.get("requestId")
    content = message.get("content")
    
    # Store task ID for potential cancellation
    task_id = f"{session_id}:{request_id}"
    
    try:
        # Get answer using the contextual QA system
        answer, context = answer_question(content)
        
        # Create response object
        response = {
            "sessionId": session_id,
            "requestId": request_id,
            "content": answer,
            "role": "BOT",
            "status": "COMPLETED",
            "context_used": context
        }
        
        return response
    except CancelledError:
        return {
            "sessionId": session_id,
            "requestId": request_id,
            "content": "This request was cancelled.",
            "role": "BOT",
            "status": "CANCELLED"
        }
    except Exception as e:
        return {
            "sessionId": session_id,
            "requestId": request_id,
            "content": f"I encountered an error: {str(e)}",
            "role": "BOT",
            "status": "ERROR"
        }
    finally:
        # Remove task from active tasks
        if task_id in active_tasks:
            del active_tasks[task_id]

def cancel_processing(message: Dict[str, Any]) -> None:
    """Cancel an ongoing processing task"""
    session_id = message.get("sessionId")
    request_id = message.get("requestId")
    
    task_id = f"{session_id}:{request_id}"
    
    # If the task is still active, cancel it
    if task_id in active_tasks:
        # Mark the task as cancelled
        active_tasks[task_id] = "CANCELLED"
        print(f"Cancelled task: {task_id}")
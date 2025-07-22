from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from app.models import ChatRequest, ChatResponse, TitleRequest, TitleResponse
from app.services.chatbot import answer_question, process_message, cancel_processing, generate_session_title
from app.config import API_TITLE, API_VERSION

# The lifespan and app initialization will be in main.py
app = None

def create_app(lifespan_handler):
    """Create and configure the FastAPI application"""
    app = FastAPI(
        title=API_TITLE, 
        version=API_VERSION,
        lifespan=lifespan_handler
    )

    # Add CORS middleware
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],  # In production, specify exact origins
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )
    
    # Define routes
    @app.get("/")
    async def root():
        return {"message": "FPT University Chatbot Service is running!"}

    @app.get("/health")
    async def health_check():
        return {"status": "healthy", "message": "Service is running"}

    @app.post("/chat", response_model=ChatResponse)
    async def chat_endpoint(request: ChatRequest):
        try:
            answer, context = answer_question(request.message)
            return ChatResponse(response=answer, context_used=context)
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Error processing request: {str(e)}")

    @app.get("/chat/{message}")
    async def chat_get(message: str):
        """Alternative GET endpoint for simple requests"""
        try:
            answer, context = answer_question(message)
            return {"response": answer, "context_used": context}
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Error processing request: {str(e)}")
    
    @app.post("/generate-title", response_model=TitleResponse)
    async def generate_title(request: TitleRequest):
        """Generate a title from a message"""
        try:
            title = generate_session_title(request.message)
            return TitleResponse(title=title)
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Error generating title: {str(e)}")
            
    return app
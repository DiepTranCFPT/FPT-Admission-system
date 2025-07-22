from pydantic import BaseModel
from typing import Optional, Dict, Any

class ChatRequest(BaseModel):
    message: str
    
class ChatResponse(BaseModel):
    response: str
    context_used: str

class TitleRequest(BaseModel):
    message: str
    
class TitleResponse(BaseModel):
    title: str

class RabbitMQMessage(BaseModel):
    sessionId: str
    requestId: str
    content: str
    role: Optional[str] = None
    status: Optional[str] = None
    context_used: Optional[str] = None
    generateTitle: Optional[bool] = False
    title: Optional[str] = None
    operation: Optional[str] = None
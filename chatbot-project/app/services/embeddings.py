import numpy as np
from typing import List, Dict, Any
from .mongodb import mongodb_service
from app.utils.helpers import load_json_file
from app.config import EMBEDDINGS_FILE

# Global variables
embedding_model = None
embeddings_data = []

def init_embedding_service(model):
    """Initialize the embedding service with model and load data"""
    global embedding_model, embeddings_data
    embedding_model = model
    
    # Try to load from MongoDB first
    embeddings_data = mongodb_service.get_all_embeddings()
    
    # If MongoDB is empty, try to load from JSON file and migrate
    if not embeddings_data:
        print("📁 No embeddings found in MongoDB, checking JSON file...")
        json_data = load_json_file(EMBEDDINGS_FILE)
        if json_data:
            print(f"📤 Migrating {len(json_data)} embeddings from JSON to MongoDB...")
            if mongodb_service.save_embeddings(json_data):
                embeddings_data = json_data
                print("✅ Successfully migrated embeddings to MongoDB")
            else:
                print("❌ Failed to migrate embeddings to MongoDB, using JSON data")
                embeddings_data = json_data
        else:
            print("⚠️ No embeddings found in JSON file either")
    else:
        print(f"✅ Loaded {len(embeddings_data)} embeddings from MongoDB")
    
    return len(embeddings_data) > 0

def cosine_similarity(vec1: List[float], vec2: List[float]) -> float:
    """Calculate cosine similarity between two vectors"""
    vec1 = np.array(vec1)
    vec2 = np.array(vec2)
    
    dot_product = np.dot(vec1, vec2)
    norm1 = np.linalg.norm(vec1)
    norm2 = np.linalg.norm(vec2)
    
    if norm1 == 0 or norm2 == 0:
        return 0
    
    return dot_product / (norm1 * norm2)

def get_embedding(text: str) -> List[float]:
    """Get embedding for a text using the embedding model"""
    if not embedding_model:
        raise ValueError("Embedding model not initialized")
    
    embeddings = embedding_model.get_embeddings([text])
    return embeddings[0].values

def retrieve_context(query: str, top_k: int = 3) -> str:
    """Retrieve the most relevant context for a query"""
    if not embeddings_data:
        return "No context available."
    
    # Get embedding for the query
    query_embedding = get_embedding(query)
    
    # Calculate similarities
    similarities = []
    for item in embeddings_data:
        if 'embedding' in item:
            similarity = cosine_similarity(query_embedding, item['embedding'])
            similarities.append((similarity, item))
    
    # Sort by similarity (descending)
    similarities.sort(key=lambda x: x[0], reverse=True)
    
    # Get top-k most similar items
    top_items = similarities[:top_k]
    
    # Create context string
    context_parts = []
    for similarity, item in top_items:
        if similarity > 0.1:  # Only include if similarity is above threshold
            context_parts.append(f"Q: {item['question']}\nA: {item['answer']}")
    
    return "\n\n".join(context_parts) if context_parts else "No relevant context found."

def add_new_embedding(question: str, answer: str) -> bool:
    """Add a new question-answer pair with embedding to the database"""
    global embeddings_data
    
    try:
        # Generate embedding
        embedding = get_embedding(question)
        
        # Add to MongoDB
        success = mongodb_service.add_embedding(question, answer, embedding)
        
        if success:
            # Update local cache
            new_item = {
                "question": question,
                "answer": answer,
                "embedding": embedding
            }
            embeddings_data.append(new_item)
            return True
        
        return False
    except Exception as e:
        print(f"❌ Error adding new embedding: {str(e)}")
        return False

def refresh_embeddings_cache():
    """Refresh the local embeddings cache from MongoDB"""
    global embeddings_data
    embeddings_data = mongodb_service.get_all_embeddings()
    print(f"🔄 Refreshed embeddings cache with {len(embeddings_data)} items")

def get_embeddings_count() -> int:
    """Get the total number of embeddings"""
    return len(embeddings_data)
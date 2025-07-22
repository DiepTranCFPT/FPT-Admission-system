from pymongo import MongoClient
from typing import List, Dict, Any, Optional
import numpy as np
from app.config import MONGODB_URL, MONGODB_DATABASE, MONGODB_COLLECTION_EMBEDDINGS, MONGODB_COLLECTION_FAQS

class MongoDBService:
    def __init__(self):
        self.client = None
        self.db = None
        self.embeddings_collection = None
        self.faqs_collection = None
        
    def connect(self) -> bool:
        """Connect to MongoDB"""
        try:
            self.client = MongoClient(MONGODB_URL)
            self.db = self.client[MONGODB_DATABASE]
            self.embeddings_collection = self.db[MONGODB_COLLECTION_EMBEDDINGS]
            self.faqs_collection = self.db[MONGODB_COLLECTION_FAQS]
            
            # Test connection
            self.client.admin.command('ping')
            print("✅ Connected to MongoDB")
            return True
        except Exception as e:
            print(f"❌ Failed to connect to MongoDB: {str(e)}")
            return False
    
    def create_indexes(self):
        """Create necessary indexes for better performance"""
        try:
            # Create index on question field for faster text searches
            self.embeddings_collection.create_index("question")
            self.faqs_collection.create_index("question")
            print("✅ MongoDB indexes created")
        except Exception as e:
            print(f"⚠️ Warning: Could not create indexes: {str(e)}")
    
    def get_all_embeddings(self) -> List[Dict[str, Any]]:
        """Get all embeddings from MongoDB"""
        try:
            embeddings = list(self.embeddings_collection.find({}, {"_id": 0}))
            return embeddings
        except Exception as e:
            print(f"❌ Error fetching embeddings from MongoDB: {str(e)}")
            return []
    
    def save_embeddings(self, embeddings_data: List[Dict[str, Any]]) -> bool:
        """Save embeddings to MongoDB"""
        try:
            # Clear existing embeddings
            self.embeddings_collection.delete_many({})
            
            # Insert new embeddings
            if embeddings_data:
                self.embeddings_collection.insert_many(embeddings_data)
            
            print(f"✅ Saved {len(embeddings_data)} embeddings to MongoDB")
            return True
        except Exception as e:
            print(f"❌ Error saving embeddings to MongoDB: {str(e)}")
            return False
    
    def get_faqs(self) -> List[Dict[str, Any]]:
        """Get all FAQs from MongoDB"""
        try:
            faqs = list(self.faqs_collection.find({}, {"_id": 0}))
            return faqs
        except Exception as e:
            print(f"❌ Error fetching FAQs from MongoDB: {str(e)}")
            return []
    
    def save_faqs(self, faqs_data: List[Dict[str, Any]]) -> bool:
        """Save FAQs to MongoDB"""
        try:
            # Clear existing FAQs
            self.faqs_collection.delete_many({})
            
            # Insert new FAQs
            if faqs_data:
                self.faqs_collection.insert_many(faqs_data)
            
            print(f"✅ Saved {len(faqs_data)} FAQs to MongoDB")
            return True
        except Exception as e:
            print(f"❌ Error saving FAQs to MongoDB: {str(e)}")
            return False
    
    def search_similar_questions(self, query: str, limit: int = 5) -> List[Dict[str, Any]]:
        """Search for similar questions using text search"""
        try:
            # Use MongoDB text search (requires text index)
            results = list(self.embeddings_collection.find(
                {"$text": {"$search": query}},
                {"_id": 0, "score": {"$meta": "textScore"}}
            ).sort([("score", {"$meta": "textScore"})]).limit(limit))
            
            return results
        except Exception as e:
            print(f"❌ Error searching questions: {str(e)}")
            return []
    
    def add_embedding(self, question: str, answer: str, embedding: List[float]) -> bool:
        """Add a single embedding to MongoDB"""
        try:
            document = {
                "question": question,
                "answer": answer,
                "embedding": embedding
            }
            self.embeddings_collection.insert_one(document)
            return True
        except Exception as e:
            print(f"❌ Error adding embedding: {str(e)}")
            return False
    
    def close(self):
        """Close MongoDB connection"""
        if self.client:
            self.client.close()
            print("🔒 MongoDB connection closed")

# Global MongoDB service instance
mongodb_service = MongoDBService()
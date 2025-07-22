"""
Migration script to move data from JSON files to MongoDB
"""
import json
from app.services.mongodb import mongodb_service
from app.utils.helpers import load_json_file
from app.config import EMBEDDINGS_FILE

def migrate_embeddings():
    """Migrate embeddings from JSON to MongoDB"""
    print("🚀 Starting migration from JSON to MongoDB...")
    
    # Connect to MongoDB
    if not mongodb_service.connect():
        print("❌ Failed to connect to MongoDB")
        return False
    
    # Load data from JSON file
    json_data = load_json_file(EMBEDDINGS_FILE)
    
    if not json_data:
        print("❌ No data found in JSON file")
        return False
    
    print(f"📁 Found {len(json_data)} records in JSON file")
    
    # Save to MongoDB
    success = mongodb_service.save_embeddings(json_data)
    
    if success:
        print("✅ Migration completed successfully!")
        mongodb_service.create_indexes()
        print("✅ Indexes created")
    else:
        print("❌ Migration failed")
    
    mongodb_service.close()
    return success

if __name__ == "__main__":
    migrate_embeddings()
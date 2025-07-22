import os

# Vertex AI configuration
PROJECT_ID = "peak-geode-461209-c1"
LOCATION = "us-central1"

# File paths (keeping for backward compatibility during migration)
EMBEDDINGS_FILE = "mongo_embeddings.json"

# MongoDB configuration
MONGODB_URL = os.getenv("MONGODB_URL", "mongodb://localhost:27017")
MONGODB_DATABASE = os.getenv("MONGODB_DATABASE", "fpt_chatbot")
MONGODB_COLLECTION_EMBEDDINGS = os.getenv("MONGODB_COLLECTION_EMBEDDINGS", "embeddings")
MONGODB_COLLECTION_FAQS = os.getenv("MONGODB_COLLECTION_FAQS", "faqs")

# RabbitMQ configuration
RABBITMQ_HOST = os.getenv("RABBITMQ_HOST", "localhost")
RABBITMQ_PORT = int(os.getenv("RABBITMQ_PORT", "5672"))
RABBITMQ_USERNAME = os.getenv("RABBITMQ_USERNAME", "guest")
RABBITMQ_PASSWORD = os.getenv("RABBITMQ_PASSWORD", "guest")

# Queue names - updated to match Java configuration
QUEUE_PYTHON_TITLE_REQUEST = "python-title-request"
QUEUE_PYTHON_TITLE_RESPONSE = "python-title-response"
QUEUE_PYTHON_MESSAGE_REQUEST = "python-processing"
QUEUE_PYTHON_MESSAGE_RESPONSE = "bot-responses"
QUEUE_PYTHON_CANCEL = "python-cancel"
EXCHANGE_PYTHON = "python-exchange"

# API configuration
API_TITLE = "FPT University Chatbot Service"
API_VERSION = "1.0.0"
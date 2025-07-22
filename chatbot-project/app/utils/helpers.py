import os
import json
from typing import List, Dict, Any

def load_json_file(file_path: str) -> List[Dict[str, Any]]:
    """Load JSON data from a file"""
    if not os.path.exists(file_path):
        return []
        
    with open(file_path, "r", encoding="utf-8") as f:
        return json.load(f)

def save_json_file(file_path: str, data: List[Dict[str, Any]]) -> bool:
    """Save data to a JSON file"""
    try:
        with open(file_path, "w", encoding="utf-8") as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
        return True
    except Exception as e:
        print(f"Error saving file {file_path}: {str(e)}")
        return False
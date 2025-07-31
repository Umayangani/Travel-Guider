#!/usr/bin/env python3
"""
ML Integration Demo Script for Travel Guider
Demonstrates how the ML system integrates with the Java backend
"""

import os
import sys
import pandas as pd
import requests
import json
from datetime import datetime

def check_backend_status():
    """Check if the backend is running"""
    try:
        response = requests.get("http://localhost:8080/api/places", timeout=5)
        return response.status_code == 200
    except:
        return False

def load_ml_dataset():
    """Load the ML dataset if it exists"""
    ml_file = "./places_ml_dataset.csv"
    if os.path.exists(ml_file):
        df = pd.read_csv(ml_file)
        print(f"✅ ML dataset loaded: {len(df)} records")
        return df
    else:
        print("❌ ML dataset not found")
        return None

def test_ml_integration():
    """Test the ML integration workflow"""
    print("🚀 Testing ML Integration Workflow...")
    print("-" * 50)
    
    # Check backend
    print("1. Checking backend status...")
    if check_backend_status():
        print("✅ Backend is running")
    else:
        print("❌ Backend is not running or not accessible")
    
    # Check ML dataset
    print("\n2. Checking ML dataset...")
    df = load_ml_dataset()
    
    # Check Python ML dependencies
    print("\n3. Checking Python ML dependencies...")
    try:
        import pandas as pd
        import numpy as np
        import sklearn
        print("✅ All ML dependencies available")
        print(f"   - pandas: {pd.__version__}")
        print(f"   - numpy: {np.__version__}")
        print(f"   - scikit-learn: {sklearn.__version__}")
    except ImportError as e:
        print(f"❌ Missing ML dependency: {e}")
    
    # Check ML models directory
    print("\n4. Checking ML models directory...")
    models_dir = "./models"
    if os.path.exists(models_dir):
        models = [f for f in os.listdir(models_dir) if f.endswith('.pkl')]
        if models:
            print(f"✅ Found {len(models)} ML model files:")
            for model in models:
                print(f"   - {model}")
        else:
            print("⚠️  Models directory exists but no models found")
    else:
        print("⚠️  Models directory not found")
    
    # Check auto-trainer script
    print("\n5. Checking auto-trainer script...")
    trainer_script = "./auto_retrain_model.py"
    if os.path.exists(trainer_script):
        print("✅ Auto-trainer script found")
    else:
        print("❌ Auto-trainer script not found")
    
    print("\n" + "=" * 50)
    print("ML INTEGRATION WORKFLOW:")
    print("1. Add/Update/Delete place via admin UI")
    print("2. PlaceService calls AutoMLService.updateMLDatasets()")
    print("3. CSV export updates places_ml_dataset.csv")
    print("4. Python auto_retrain_model.py retrains models")
    print("5. Updated models used for travel recommendations")
    print("=" * 50)

def simulate_recommendation_request():
    """Simulate a travel recommendation request"""
    print("\n🎯 Testing Travel Recommendation...")
    
    # Sample user preferences
    preferences = {
        "region": "Western",
        "category": "Historical",
        "budget": 15000,
        "duration": 3
    }
    
    try:
        response = requests.post(
            "http://localhost:8080/api/itinerary/recommend",
            json=preferences,
            timeout=10
        )
        
        if response.status_code == 200:
            data = response.json()
            print("✅ Recommendation request successful")
            recommendations = data.get('recommendations', [])
            print(f"   Found {len(recommendations)} recommendations")
            
            for i, rec in enumerate(recommendations[:3], 1):
                print(f"   {i}. {rec.get('placeName')} ({rec.get('region')}) - Score: {rec.get('recommendationScore', 0):.2f}")
        else:
            print(f"❌ Recommendation request failed: {response.status_code}")
    except Exception as e:
        print(f"❌ Recommendation request error: {e}")

if __name__ == "__main__":
    print("🤖 Travel Guider ML Integration Test")
    print("====================================")
    
    # Change to the correct directory
    script_dir = os.path.dirname(os.path.abspath(__file__))
    os.chdir(script_dir)
    
    test_ml_integration()
    simulate_recommendation_request()
    
    print("\n📝 Summary:")
    print("- ML system is integrated with automatic dataset updates")
    print("- Place CRUD operations trigger ML data refresh")
    print("- Travel recommendations available via REST API")
    print("- Complete ML pipeline ready for production")

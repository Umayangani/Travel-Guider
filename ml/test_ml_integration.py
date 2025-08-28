"""
Test ML Integration with Backend
===============================
This script tests the integration between the ML models and the backend API.
"""

import requests
import json
import pandas as pd
import sys
import os

def test_backend_connection():
    """Test if backend is running"""
    try:
        response = requests.get("http://localhost:8090/api/places", timeout=5)
        print(f"✅ Backend is running (Status: {response.status_code})")
        return True
    except requests.exceptions.RequestException as e:
        print(f"❌ Backend connection failed: {e}")
        return False

def test_csv_import():
    """Test CSV import functionality"""
    try:
        response = requests.post("http://localhost:8090/api/csv/import/places", timeout=30)
        if response.status_code == 200:
            result = response.json()
            print(f"✅ CSV import successful: {result.get('message', 'Done')}")
            return True
        else:
            print(f"❌ CSV import failed (Status: {response.status_code})")
            print(f"Response: {response.text}")
            return False
    except requests.exceptions.RequestException as e:
        print(f"❌ CSV import request failed: {e}")
        return False

def test_itinerary_generation():
    """Test itinerary generation with sample data"""
    test_request = {
        "title": "Test ML Itinerary",
        "startDate": "2025-09-01",
        "endDate": "2025-09-03",
        "startingLocation": "Colombo",
        "adultsCount": 2,
        "childrenCount": 0,
        "studentsCount": 0,
        "foreignersCount": 0,
        "budgetRange": "medium",
        "transportPreference": "bus",
        "activityLevel": "moderate",
        "preferredCategories": ["Beach", "Temple", "Scenic"],
        "specificInterests": ["Beach", "Temple", "Scenic"],
        "includeWeather": True
    }
    
    try:
        response = requests.post(
            "http://localhost:8090/api/itinerary/generate?userId=1",
            json=test_request,
            headers={"Content-Type": "application/json"},
            timeout=60
        )
        
        if response.status_code == 200:
            result = response.json()
            print("✅ Itinerary generation successful!")
            print(f"📋 Title: {result.get('title', 'N/A')}")
            print(f"📅 Days: {result.get('totalDays', 'N/A')}")
            print(f"👥 People: {result.get('totalPeople', 'N/A')}")
            print(f"💰 Cost: LKR {result.get('totalEstimatedCost', 'N/A')}")
            
            if 'days' in result:
                print(f"🗓️ Daily plans: {len(result['days'])} days planned")
                for i, day in enumerate(result['days'][:2]):  # Show first 2 days
                    places_count = len(day.get('places', []))
                    print(f"  Day {i+1}: {places_count} places")
            
            return True
        else:
            print(f"❌ Itinerary generation failed (Status: {response.status_code})")
            print(f"Response: {response.text}")
            return False
            
    except requests.exceptions.RequestException as e:
        print(f"❌ Itinerary generation request failed: {e}")
        return False

def check_data_files():
    """Check if required data files exist"""
    files_to_check = [
        "../backend/uploads/places.csv",
        "../backend/uploads/places_ml_ready.csv"
    ]
    
    for file_path in files_to_check:
        if os.path.exists(file_path):
            df = pd.read_csv(file_path)
            print(f"✅ {file_path}: {len(df)} rows")
        else:
            print(f"❌ {file_path}: File not found")

def run_ml_training():
    """Run the ML training script"""
    print("🤖 Running ML training...")
    try:
        from auto_retrain_model import TravelPlacesMLTrainer
        trainer = TravelPlacesMLTrainer()
        results = trainer.train_complete_pipeline()
        print("✅ ML training completed successfully!")
        return True
    except Exception as e:
        print(f"❌ ML training failed: {e}")
        return False

def main():
    """Run all tests"""
    print("🔬 Testing Travel Guider ML Integration")
    print("=" * 50)
    
    # Check data files
    print("\n📁 Checking data files...")
    check_data_files()
    
    # Test backend connection
    print("\n🌐 Testing backend connection...")
    backend_ok = test_backend_connection()
    
    if not backend_ok:
        print("❌ Cannot proceed without backend. Please start the backend first.")
        return
    
    # Test CSV import
    print("\n📥 Testing CSV import...")
    csv_ok = test_csv_import()
    
    # Run ML training
    print("\n🤖 Testing ML training...")
    ml_ok = run_ml_training()
    
    # Test itinerary generation
    print("\n🎯 Testing itinerary generation...")
    itinerary_ok = test_itinerary_generation()
    
    # Summary
    print("\n" + "=" * 50)
    print("📊 Test Summary:")
    print(f"Backend Connection: {'✅' if backend_ok else '❌'}")
    print(f"CSV Import: {'✅' if csv_ok else '❌'}")
    print(f"ML Training: {'✅' if ml_ok else '❌'}")
    print(f"Itinerary Generation: {'✅' if itinerary_ok else '❌'}")
    
    if all([backend_ok, csv_ok, ml_ok, itinerary_ok]):
        print("\n🎉 All tests passed! Your ML-powered travel system is ready!")
    else:
        print("\n⚠️ Some tests failed. Check the logs above for details.")

if __name__ == "__main__":
    main()

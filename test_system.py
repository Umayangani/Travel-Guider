#!/usr/bin/env python3
"""
Travel Guider System Test
Test all three services: ML, Backend, Frontend
"""

import requests
import json
import time

def test_ml_service():
    """Test ML service health and itinerary generation"""
    print("🧪 Testing ML Service...")
    
    try:
        # Health check
        response = requests.get('http://localhost:5000/health')
        if response.status_code == 200:
            data = response.json()
            print(f"✅ ML Service healthy: {data['places_loaded']} places loaded")
        else:
            print("❌ ML Service health check failed")
            return False
            
        # Test itinerary generation
        test_request = {
            "preferences": ["Temple", "Nature"],
            "total_days": 3,
            "transport_mode": "public",
            "places_per_day": 3
        }
        
        response = requests.post(
            'http://localhost:5000/api/ml/optimize-itinerary',
            json=test_request,
            headers={'Content-Type': 'application/json'}
        )
        
        if response.status_code == 200:
            data = response.json()
            if data.get('success'):
                itinerary = data['itinerary']
                print(f"✅ Itinerary generated: {itinerary['total_days']} days, {itinerary['total_places']} places")
                return True
            else:
                print("❌ Itinerary generation failed")
                return False
        else:
            print(f"❌ ML API request failed: {response.status_code}")
            return False
            
    except Exception as e:
        print(f"❌ ML Service test error: {e}")
        return False

def test_backend_service():
    """Test backend service health"""
    print("🧪 Testing Backend Service...")
    
    try:
        # Try to access backend places endpoint
        response = requests.get('http://localhost:8090/api/places')
        if response.status_code == 200:
            places = response.json()
            print(f"✅ Backend service healthy: {len(places)} places available")
            return True
        else:
            print(f"❌ Backend service not responding: {response.status_code}")
            return False
            
    except Exception as e:
        print(f"❌ Backend service test error: {e}")
        return False

def test_frontend_service():
    """Test frontend service availability"""
    print("🧪 Testing Frontend Service...")
    
    try:
        response = requests.get('http://localhost:3000')
        if response.status_code == 200:
            print("✅ Frontend service healthy")
            return True
        else:
            print(f"❌ Frontend service not responding: {response.status_code}")
            return False
            
    except Exception as e:
        print(f"❌ Frontend service test error: {e}")
        return False

def test_full_integration():
    """Test full system integration"""
    print("🧪 Testing Full System Integration...")
    
    try:
        # Test backend to ML integration
        test_request = {
            "title": "Test Trip",
            "startDate": "2024-12-01",
            "endDate": "2024-12-03",
            "totalDays": 3,
            "startingLocation": "Colombo",
            "adultsCount": 2,
            "childrenCount": 0,
            "transportPreference": "bus",
            "preferredCategories": ["Temple", "Nature"],
            "budgetRange": "medium"
        }
        
        response = requests.post(
            'http://localhost:8090/api/itinerary/generate',
            json=test_request,
            headers={'Content-Type': 'application/json'}
        )
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Full integration working: Generated itinerary with {len(data.get('days', []))} days")
            return True
        else:
            print(f"❌ Integration test failed: {response.status_code}")
            return False
            
    except Exception as e:
        print(f"❌ Integration test error: {e}")
        return False

def main():
    """Run all tests"""
    print("🚀 TRAVEL GUIDER SYSTEM TEST")
    print("=" * 50)
    
    tests_passed = 0
    total_tests = 4
    
    # Test individual services
    if test_ml_service():
        tests_passed += 1
    
    if test_backend_service():
        tests_passed += 1
        
    if test_frontend_service():
        tests_passed += 1
        
    if test_full_integration():
        tests_passed += 1
    
    print("\n" + "=" * 50)
    print(f"🎯 TEST RESULTS: {tests_passed}/{total_tests} tests passed")
    
    if tests_passed == total_tests:
        print("🎉 ALL SYSTEMS OPERATIONAL!")
        print("🌐 Frontend: http://localhost:3000")
        print("🔧 Backend: http://localhost:8090")
        print("🤖 ML Service: http://localhost:5000")
    else:
        print("⚠️ Some services are not running. Please check:")
        print("  1. Backend: mvn spring-boot:run")
        print("  2. Frontend: npm start")
        print("  3. ML Service: python ml_service.py")

if __name__ == "__main__":
    main()

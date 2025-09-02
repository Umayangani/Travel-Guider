#!/usr/bin/env python3
"""
Travel Guider ML Service
Standalone Flask API for travel itinerary generation
"""

import pandas as pd
import numpy as np
import json
import math
import random
from datetime import datetime, timedelta
from flask import Flask, request, jsonify
from flask_cors import CORS
import warnings
warnings.filterwarnings('ignore')

class TravelMLService:
    """Complete Travel ML Service"""
    
    def __init__(self, csv_path='../backend/uploads/places.csv'):
        self.csv_path = csv_path
        self.places_df = None
        self.colombo_lat = 6.9271
        self.colombo_lon = 79.8612
        self.load_data()
    
    def load_data(self):
        """Load places data with encoding handling"""
        try:
            for encoding in ['utf-8', 'latin-1', 'cp1252']:
                try:
                    self.places_df = pd.read_csv(self.csv_path, encoding=encoding)
                    print(f"✅ Data loaded with {encoding} encoding: {len(self.places_df)} places")
                    return True
                except UnicodeDecodeError:
                    continue
            print("❌ Failed to load data with any encoding")
            return False
        except Exception as e:
            print(f"❌ Error loading data: {e}")
            return False
    
    def haversine_distance(self, lat1, lon1, lat2, lon2):
        """Calculate distance between two points"""
        if pd.isna(lat1) or pd.isna(lon1) or pd.isna(lat2) or pd.isna(lon2):
            return 0
        
        lat1, lon1, lat2, lon2 = map(math.radians, [lat1, lon1, lat2, lon2])
        dlat = lat2 - lat1
        dlon = lon2 - lon1
        a = math.sin(dlat/2)**2 + math.cos(lat1) * math.cos(lat2) * math.sin(dlon/2)**2
        c = 2 * math.asin(math.sqrt(a))
        return c * 6371  # Earth radius in km
    
    def calculate_travel_time(self, distance_km, transport_mode='public'):
        """Calculate travel time"""
        if distance_km <= 0:
            return 0
        
        speeds = {'public': 35, 'private': 50, 'walking': 5}
        speed = speeds.get(transport_mode, 35)
        travel_time = distance_km / speed
        
        if distance_km > 50:
            travel_time += 0.5  # Break time
        
        return round(travel_time, 2)
    
    def generate_itinerary(self, preferences, total_days, transport_mode='public', places_per_day=3):
        """Generate travel itinerary"""
        if self.places_df is None:
            return {'error': 'Data not loaded'}
        
        print(f"🎯 Generating {total_days}-day itinerary for {preferences}")
        
        # Filter places by preferences
        filtered_places = self._filter_places(preferences)
        
        # Generate daily schedule
        daily_schedule = []
        
        for day in range(1, total_days + 1):
            if total_days == 1:
                day_places = self._get_colombo_area_places(filtered_places, places_per_day)
            elif day == 1:
                day_places = self._get_diverse_places(filtered_places, places_per_day)
            elif day == total_days:
                day_places = self._get_return_places(filtered_places, places_per_day)
            else:
                day_places = self._get_regional_places(filtered_places, places_per_day)
            
            daily_schedule.append(self._create_day_schedule(day_places, day, transport_mode))
        
        return {
            'total_days': total_days,
            'total_places': sum(len(day['places']) for day in daily_schedule),
            'daily_itinerary': daily_schedule,
            'generated_by': 'Travel ML System',
            'generation_time': datetime.now().isoformat()
        }
    
    def _filter_places(self, preferences):
        """Filter places by preferences"""
        if not preferences:
            return self.places_df.sample(min(50, len(self.places_df))).to_dict('records')
        
        filtered = []
        for _, place in self.places_df.iterrows():
            category = str(place.get('Category', '')).lower()
            if any(pref.lower() in category for pref in preferences):
                filtered.append(place.to_dict())
        
        if not filtered:
            filtered = self.places_df.sample(min(30, len(self.places_df))).to_dict('records')
        
        return filtered
    
    def _get_colombo_area_places(self, places, count):
        """Get places near Colombo"""
        colombo_places = []
        for place in places:
            distance = self.haversine_distance(
                self.colombo_lat, self.colombo_lon,
                place.get('Latitude', 0), place.get('Longitude', 0)
            )
            if distance <= 50:
                place['distance_from_colombo'] = distance
                colombo_places.append(place)
        
        colombo_places.sort(key=lambda x: x['distance_from_colombo'])
        return colombo_places[:count]
    
    def _get_diverse_places(self, places, count):
        """Get diverse places for multi-day trips"""
        return random.sample(places, min(count, len(places)))
    
    def _get_regional_places(self, places, count):
        """Get places from specific regions"""
        return random.sample(places, min(count, len(places)))
    
    def _get_return_places(self, places, count):
        """Get places for return journey"""
        return self._get_colombo_area_places(places, count)
    
    def _create_day_schedule(self, places, day_number, transport_mode):
        """Create detailed day schedule"""
        if not places:
            return {
                'day': day_number,
                'places': [],
                'total_distance_km': 0,
                'total_travel_time_hours': 0
            }
        
        enhanced_places = []
        total_distance = 0
        total_travel_time = 0
        
        for i, place in enumerate(places):
            enhanced_place = place.copy()
            
            if i == 0:
                distance = self.haversine_distance(
                    self.colombo_lat, self.colombo_lon,
                    place.get('Latitude', 0), place.get('Longitude', 0)
                )
            else:
                prev_place = places[i-1]
                distance = self.haversine_distance(
                    prev_place.get('Latitude', 0), prev_place.get('Longitude', 0),
                    place.get('Latitude', 0), place.get('Longitude', 0)
                )
            
            enhanced_place['distance_from_previous_km'] = distance
            enhanced_place['travel_time_from_previous_hours'] = self.calculate_travel_time(distance, transport_mode)
            enhanced_place['estimated_visit_time_hours'] = place.get('Eestimated_time_to_visit', 2)
            
            total_distance += distance
            total_travel_time += enhanced_place['travel_time_from_previous_hours']
            enhanced_places.append(enhanced_place)
        
        return {
            'day': day_number,
            'places': enhanced_places,
            'total_distance_km': round(total_distance, 2),
            'total_travel_time_hours': round(total_travel_time, 2)
        }

# Initialize Flask app
app = Flask(__name__)
CORS(app, origins=['http://localhost:3000'])

# Initialize ML service
ml_service = TravelMLService()

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({
        'status': 'healthy',
        'service': 'Travel ML System',
        'places_loaded': len(ml_service.places_df) if ml_service.places_df is not None else 0,
        'timestamp': datetime.now().isoformat()
    })

@app.route('/api/ml/optimize-itinerary', methods=['POST'])
def optimize_itinerary():
    try:
        data = request.get_json()
        preferences = data.get('preferences', [])
        total_days = data.get('total_days', 3)
        transport_mode = data.get('transport_mode', 'public')
        places_per_day = data.get('places_per_day', 3)
        
        itinerary = ml_service.generate_itinerary(
            preferences, total_days, transport_mode, places_per_day
        )
        
        return jsonify({
            'success': True,
            'itinerary': itinerary
        })
        
    except Exception as e:
        return jsonify({
            'success': False,
            'error': str(e)
        }), 500

if __name__ == '__main__':
    print("🚀 Starting Travel ML Service...")
    print("🔗 Health check: http://localhost:5000/health")
    print("🎯 Optimize itinerary: POST http://localhost:5000/api/ml/optimize-itinerary")
    app.run(host='0.0.0.0', port=5000, debug=True)

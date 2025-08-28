from flask import Flask, request, jsonify
from flask_cors import CORS
import pandas as pd
import numpy as np
from itinerary_optimizer import ItineraryOptimizer
import os

app = Flask(__name__)
CORS(app)  # Enable CORS for React frontend

# Initialize the optimizer
optimizer = None

def initialize_optimizer():
    """Initialize the ML optimizer with CSV data"""
    global optimizer
    csv_path = os.path.join('..', 'backend', 'uploads', 'places.csv')
    
    if os.path.exists(csv_path):
        optimizer = ItineraryOptimizer(csv_path)
        print(f"✅ ML Optimizer initialized with data from {csv_path}")
        return True
    else:
        print(f"❌ CSV file not found at {csv_path}")
        return False

@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({
        'status': 'healthy',
        'service': 'Travel Guider ML API',
        'optimizer_loaded': optimizer is not None
    })

@app.route('/api/ml/optimize-itinerary', methods=['POST'])
def optimize_itinerary():
    """Enhanced ML-powered itinerary optimization"""
    try:
        if not optimizer:
            return jsonify({'error': 'ML optimizer not initialized'}), 500
        
        data = request.get_json()
        
        # Extract parameters
        start_location = data.get('startingLocation', 'Colombo')
        end_location = data.get('endingLocation', start_location)
        duration_days = data.get('duration', 3)
        travel_style = data.get('travelStyle', 'Moderate')
        interests = data.get('interests', [])
        
        # Generate optimized itinerary
        result = optimizer.generate_optimized_itinerary(
            start_location=start_location,
            end_location=end_location,
            duration_days=duration_days,
            travel_style=travel_style,
            interests=interests
        )
        
        return jsonify(result)
        
    except Exception as e:
        print(f"❌ Error in optimize_itinerary: {str(e)}")
        return jsonify({'error': f'ML optimization failed: {str(e)}'}), 500

@app.route('/api/ml/places-by-region', methods=['GET'])
def get_places_by_region():
    """Get places filtered by region and interests"""
    try:
        if not optimizer:
            return jsonify({'error': 'ML optimizer not initialized'}), 500
        
        region = request.args.get('region', 'West')
        interests = request.args.get('interests', '').split(',') if request.args.get('interests') else None
        max_places = int(request.args.get('max_places', 20))
        
        places = optimizer.get_places_by_region(region, interests, max_places)
        
        return jsonify({
            'success': True,
            'region': region,
            'interests': interests,
            'total_places': len(places),
            'places': places
        })
        
    except Exception as e:
        print(f"❌ Error in get_places_by_region: {str(e)}")
        return jsonify({'error': f'Failed to get places: {str(e)}'}), 500

@app.route('/api/ml/route-optimization', methods=['POST'])
def optimize_route():
    """Optimize route for given places"""
    try:
        if not optimizer:
            return jsonify({'error': 'ML optimizer not initialized'}), 500
        
        data = request.get_json()
        places = data.get('places', [])
        
        if not places:
            return jsonify({'error': 'No places provided'}), 400
        
        optimized_places = optimizer.optimize_route(places)
        
        return jsonify({
            'success': True,
            'original_count': len(places),
            'optimized_count': len(optimized_places),
            'optimized_places': optimized_places
        })
        
    except Exception as e:
        print(f"❌ Error in optimize_route: {str(e)}")
        return jsonify({'error': f'Route optimization failed: {str(e)}'}), 500

@app.route('/api/ml/analytics', methods=['GET'])
def get_analytics():
    """Get analytics about the places dataset"""
    try:
        if not optimizer or optimizer.places_data is None:
            return jsonify({'error': 'ML optimizer not initialized'}), 500
        
        df = optimizer.places_data
        
        analytics = {
            'total_places': len(df),
            'regions': df['Region'].value_counts().to_dict() if 'Region' in df.columns else {},
            'categories': df['Category'].value_counts().to_dict() if 'Category' in df.columns else {},
            'average_ticket_price': df['Ticket_price'].mean() if 'Ticket_price' in df.columns else 0,
            'price_range': {
                'min': df['Ticket_price'].min() if 'Ticket_price' in df.columns else 0,
                'max': df['Ticket_price'].max() if 'Ticket_price' in df.columns else 0
            },
            'data_completeness': {
                'places_with_coordinates': len(df.dropna(subset=['Latitude', 'Longitude'])) if 'Latitude' in df.columns else 0,
                'places_with_descriptions': len(df.dropna(subset=['Description'])) if 'Description' in df.columns else 0,
                'places_with_contact': len(df.dropna(subset=['Contact_no'])) if 'Contact_no' in df.columns else 0
            }
        }
        
        return jsonify({
            'success': True,
            'analytics': analytics
        })
        
    except Exception as e:
        print(f"❌ Error in get_analytics: {str(e)}")
        return jsonify({'error': f'Analytics generation failed: {str(e)}'}), 500

@app.route('/api/ml/reload-data', methods=['POST'])
def reload_data():
    """Reload CSV data (useful after data updates)"""
    try:
        success = initialize_optimizer()
        
        if success:
            return jsonify({
                'success': True,
                'message': 'ML optimizer data reloaded successfully',
                'total_places': len(optimizer.places_data) if optimizer.places_data is not None else 0
            })
        else:
            return jsonify({'error': 'Failed to reload ML optimizer data'}), 500
            
    except Exception as e:
        print(f"❌ Error in reload_data: {str(e)}")
        return jsonify({'error': f'Data reload failed: {str(e)}'}), 500

if __name__ == '__main__':
    print("🚀 Starting Travel Guider ML API...")
    
    # Initialize the optimizer
    if initialize_optimizer():
        print("✅ ML service ready!")
    else:
        print("⚠️  ML service starting without data - will attempt to load later")
    
    # Start the Flask app
    app.run(debug=True, host='0.0.0.0', port=5000)

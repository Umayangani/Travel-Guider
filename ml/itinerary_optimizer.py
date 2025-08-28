import pandas as pd
import numpy as np
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler
from sklearn.metrics.pairwise import haversine_distances
import math
import json
import sys

class ItineraryOptimizer:
    def __init__(self, places_csv_path=None):
        """Initialize the itinerary optimizer with places data"""
        self.places_data = None
        self.scaler = StandardScaler()
        
        if places_csv_path:
            self.load_places_data(places_csv_path)
    
    def load_places_data(self, csv_path):
        """Load places data from CSV file"""
        try:
            self.places_data = pd.read_csv(csv_path)
            print(f"Loaded {len(self.places_data)} places from {csv_path}")
        except Exception as e:
            print(f"Error loading places data: {e}")
            return False
        return True
    
    def calculate_distance(self, lat1, lon1, lat2, lon2):
        """Calculate distance between two points using haversine formula"""
        # Convert to radians
        lat1, lon1, lat2, lon2 = map(math.radians, [lat1, lon1, lat2, lon2])
        
        # Haversine formula
        dlat = lat2 - lat1
        dlon = lon2 - lon1
        a = math.sin(dlat/2)**2 + math.cos(lat1) * math.cos(lat2) * math.sin(dlon/2)**2
        c = 2 * math.asin(math.sqrt(a))
        r = 6371  # Radius of earth in kilometers
        
        return c * r
    
    def get_places_by_region(self, region, interests=None, max_places=20):
        """Get places filtered by region and interests"""
        if self.places_data is None:
            return []
        
        # Filter by region
        filtered_places = self.places_data[
            self.places_data['Region'].str.contains(region, case=False, na=False)
        ].copy()
        
        # Filter by interests if provided
        if interests:
            interest_filter = filtered_places['Category'].str.contains(
                '|'.join(interests), case=False, na=False
            )
            filtered_places = filtered_places[interest_filter]
        
        # Sort by popularity or rating (if available) and limit results
        return filtered_places.head(max_places).to_dict('records')
    
    def optimize_route(self, places, start_location=None):
        """Optimize the route using nearest neighbor algorithm"""
        if not places or len(places) < 2:
            return places
        
        # Create distance matrix
        n = len(places)
        distances = np.zeros((n, n))
        
        for i in range(n):
            for j in range(n):
                if i != j:
                    lat1, lon1 = places[i].get('Latitude', 0), places[i].get('Longitude', 0)
                    lat2, lon2 = places[j].get('Latitude', 0), places[j].get('Longitude', 0)
                    distances[i][j] = self.calculate_distance(lat1, lon1, lat2, lon2)
        
        # Simple nearest neighbor optimization
        visited = [False] * n
        route = []
        current = 0  # Start from first place
        visited[current] = True
        route.append(places[current])
        
        for _ in range(n - 1):
            min_dist = float('inf')
            next_place = -1
            
            for j in range(n):
                if not visited[j] and distances[current][j] < min_dist:
                    min_dist = distances[current][j]
                    next_place = j
            
            if next_place != -1:
                visited[next_place] = True
                route.append(places[next_place])
                current = next_place
        
        return route
    
    def create_daily_itinerary(self, places, duration_days, travel_style="Moderate"):
        """Create daily itinerary based on places and duration"""
        if not places:
            return []
        
        # Calculate places per day
        places_per_day = max(1, len(places) // duration_days)
        if travel_style.lower() == "relaxed":
            places_per_day = max(1, places_per_day - 1)
        elif travel_style.lower() == "adventure":
            places_per_day = min(len(places), places_per_day + 1)
        
        # Create daily schedule
        daily_itinerary = []
        place_index = 0
        
        for day in range(1, duration_days + 1):
            daily_places = []
            day_start_time = "08:00"
            current_time = 8.0  # 8:00 AM in decimal hours
            
            for _ in range(places_per_day):
                if place_index >= len(places):
                    break
                
                place = places[place_index].copy()
                
                # Add estimated time
                visit_duration = place.get('Eestimated_time_to_visit', 2.0)
                if isinstance(visit_duration, str):
                    try:
                        visit_duration = float(visit_duration)
                    except:
                        visit_duration = 2.0
                
                # Format time
                start_hour = int(current_time)
                start_minute = int((current_time - start_hour) * 60)
                place['start_time'] = f"{start_hour:02d}:{start_minute:02d}"
                
                end_time = current_time + visit_duration
                end_hour = int(end_time)
                end_minute = int((end_time - end_hour) * 60)
                place['end_time'] = f"{end_hour:02d}:{end_minute:02d}"
                
                daily_places.append(place)
                current_time = end_time + 0.5  # 30 min travel buffer
                place_index += 1
            
            if daily_places:
                daily_itinerary.append({
                    'day': day,
                    'date': f"Day {day}",
                    'places': daily_places,
                    'total_places': len(daily_places)
                })
        
        return daily_itinerary
    
    def generate_optimized_itinerary(self, start_location, end_location, 
                                   duration_days, travel_style, interests):
        """Generate complete optimized itinerary"""
        try:
            # Determine region based on start/end locations
            region = self.determine_region(start_location, end_location)
            
            # Get relevant places
            places = self.get_places_by_region(region, interests, max_places=duration_days * 3)
            
            if not places:
                return {'error': 'No places found for the specified criteria'}
            
            # Optimize route
            optimized_places = self.optimize_route(places)
            
            # Create daily itinerary
            daily_itinerary = self.create_daily_itinerary(
                optimized_places, duration_days, travel_style
            )
            
            return {
                'success': True,
                'total_days': duration_days,
                'total_places': len(optimized_places),
                'travel_style': travel_style,
                'interests': interests,
                'daily_itinerary': daily_itinerary,
                'all_places': optimized_places
            }
            
        except Exception as e:
            return {'error': f'Error generating itinerary: {str(e)}'}
    
    def determine_region(self, start_location, end_location):
        """Determine the region based on start and end locations"""
        # Sri Lankan regions mapping
        western_cities = ['colombo', 'gampaha', 'kalutara', 'negombo']
        southern_cities = ['galle', 'matara', 'hambantota', 'mirissa']
        central_cities = ['kandy', 'nuwara eliya', 'badulla', 'ella']
        northern_cities = ['jaffna', 'vavuniya', 'mannar']
        eastern_cities = ['batticaloa', 'trincomalee', 'ampara']
        
        start_lower = start_location.lower()
        end_lower = end_location.lower()
        
        if any(city in start_lower or city in end_lower for city in western_cities):
            return 'West'
        elif any(city in start_lower or city in end_lower for city in southern_cities):
            return 'South'
        elif any(city in start_lower or city in end_lower for city in central_cities):
            return 'Central'
        elif any(city in start_lower or city in end_lower for city in northern_cities):
            return 'North'
        elif any(city in start_lower or city in end_lower for city in eastern_cities):
            return 'East'
        else:
            return 'West'  # Default to West (Colombo area)


def main():
    """Main function for command line usage"""
    if len(sys.argv) < 6:
        print("Usage: python itinerary_optimizer.py <start> <end> <days> <style> <interests>")
        return
    
    start_location = sys.argv[1]
    end_location = sys.argv[2]
    duration_days = int(sys.argv[3])
    travel_style = sys.argv[4]
    interests = sys.argv[5].split(',')
    
    # Initialize optimizer
    optimizer = ItineraryOptimizer('../backend/uploads/places.csv')
    
    # Generate itinerary
    result = optimizer.generate_optimized_itinerary(
        start_location, end_location, duration_days, travel_style, interests
    )
    
    # Output as JSON
    print(json.dumps(result, indent=2))


if __name__ == "__main__":
    main()

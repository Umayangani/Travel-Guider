package com.travelguider.backend.service;

import org.springframework.stereotype.Service;

@Service
public class DistanceCalculationService {
    
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    /**
     * Calculate distance between two points using Haversine formula
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                  Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                  Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * Calculate estimated travel time based on distance and transport mode
     */
    public double calculateTravelTime(double distanceKm, String transportMode) {
        double averageSpeed;
        
        switch (transportMode.toUpperCase()) {
            case "BUS":
                averageSpeed = 40.0; // km/h
                break;
            case "TRAIN":
                averageSpeed = 50.0; // km/h
                break;
            case "TAXI":
            case "CAR":
                averageSpeed = 60.0; // km/h
                break;
            case "WALK":
                averageSpeed = 5.0; // km/h
                break;
            default:
                averageSpeed = 40.0; // Default to bus speed
        }
        
        return distanceKm / averageSpeed;
    }
    
    /**
     * Estimate transport cost based on distance and mode
     */
    public double estimateTransportCost(double distanceKm, String transportMode, int numberOfPeople) {
        double baseCostPerKm;
        
        switch (transportMode.toUpperCase()) {
            case "BUS":
                baseCostPerKm = 3.0; // LKR per km per person
                break;
            case "TRAIN":
                baseCostPerKm = 2.5; // LKR per km per person
                break;
            case "TAXI":
                baseCostPerKm = 50.0; // LKR per km (total for vehicle)
                return distanceKm * baseCostPerKm; // Taxi cost is per vehicle
            case "WALK":
                return 0.0; // Walking is free
            default:
                baseCostPerKm = 3.0;
        }
        
        return distanceKm * baseCostPerKm * numberOfPeople;
    }
    
    /**
     * Get recommended transport mode based on distance
     */
    public String getRecommendedTransport(double distanceKm) {
        if (distanceKm <= 2.0) {
            return "WALK";
        } else if (distanceKm <= 50.0) {
            return "BUS";
        } else if (distanceKm <= 200.0) {
            return "TRAIN";
        } else {
            return "TAXI";
        }
    }
}

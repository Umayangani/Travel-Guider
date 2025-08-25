package com.travelguider.backend.service;

import com.travelguider.backend.entity.Place;
import com.travelguider.backend.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItineraryService {
    
    @Autowired
    private PlaceRepository placeRepository;
    
    public List<Map<String, Object>> generateRecommendations(Map<String, Object> preferences) {
        // Get user preferences
        String preferredRegion = (String) preferences.get("region");
        String preferredCategory = (String) preferences.get("category");
        Integer budget = (Integer) preferences.getOrDefault("budget", 10000);
        Integer duration = (Integer) preferences.getOrDefault("duration", 3);
        
        // Fetch all places
        List<Place> allPlaces = placeRepository.findAll();
        
        // Filter places based on preferences
        List<Place> filteredPlaces = allPlaces.stream()
            .filter(place -> preferredRegion == null || place.getRegion().equalsIgnoreCase(preferredRegion))
            .filter(place -> preferredCategory == null || place.getCategory().equalsIgnoreCase(preferredCategory))
            .collect(Collectors.toList());
        
        // Generate recommendations
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        for (Place place : filteredPlaces.stream().limit(duration * 2).collect(Collectors.toList())) {
            Map<String, Object> recommendation = new HashMap<>();
            recommendation.put("placeId", place.getPlaceId());
            recommendation.put("placeName", place.getName());
            recommendation.put("region", place.getRegion());
            recommendation.put("district", place.getDistrict());
            recommendation.put("category", place.getCategory());
            recommendation.put("description", place.getDescription());
            recommendation.put("coordinates", Map.of(
                "latitude", place.getLatitude(),
                "longitude", place.getLongitude()
            ));
            
            // Calculate recommendation score based on simple criteria
            double score = calculateRecommendationScore(place, preferences);
            recommendation.put("recommendationScore", score);
            
            recommendations.add(recommendation);
        }
        
        // Sort by recommendation score
        recommendations.sort((a, b) -> Double.compare(
            (Double) b.get("recommendationScore"),
            (Double) a.get("recommendationScore")
        ));
        
        return recommendations.stream().limit(duration).collect(Collectors.toList());
    }
    
    private double calculateRecommendationScore(Place place, Map<String, Object> preferences) {
        double score = 0.5; // Base score
        
        String preferredRegion = (String) preferences.get("region");
        String preferredCategory = (String) preferences.get("category");
        
        // Region match bonus
        if (preferredRegion != null && place.getRegion().equalsIgnoreCase(preferredRegion)) {
            score += 0.3;
        }
        
        // Category match bonus
        if (preferredCategory != null && place.getCategory().equalsIgnoreCase(preferredCategory)) {
            score += 0.2;
        }
        
        // Add some randomness for variety
        score += Math.random() * 0.1;
        
        return Math.min(1.0, score);
    }
    
    public List<Map<String, Object>> getAvailableDestinations() {
        List<Place> places = placeRepository.findAll();
        
        return places.stream()
            .map(place -> {
                Map<String, Object> destination = new HashMap<>();
                destination.put("placeId", place.getPlaceId());
                destination.put("placeName", place.getName());
                destination.put("region", place.getRegion());
                destination.put("district", place.getDistrict());
                destination.put("category", place.getCategory());
                return destination;
            })
            .collect(Collectors.toList());
    }
}

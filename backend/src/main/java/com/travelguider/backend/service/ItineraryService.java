package com.travelguider.backend.service;

import com.travelguider.backend.dto.ItineraryRequestDTO;
import com.travelguider.backend.dto.ItineraryResponseDTO;
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
    
    /**
     * Generate itinerary for the controller
     */
    public ItineraryResponseDTO generateItinerary(Long userId, ItineraryRequestDTO request) {
        try {
            // Create simple itinerary response based on preferences
            List<Place> allPlaces = placeRepository.findAll();
            
            // Filter places based on preferences
            List<Place> filteredPlaces = allPlaces.stream()
                .filter(place -> matchesCategories(place, request.getPreferredCategories()))
                .limit(request.getTotalDays() * 2L) // 2 places per day
                .collect(Collectors.toList());
            
            // Create response
            ItineraryResponseDTO response = new ItineraryResponseDTO();
            response.setId(System.currentTimeMillis()); // Simple ID
            response.setTitle(request.getTitle() != null ? request.getTitle() : "Travel Itinerary");
            response.setStartDate(request.getStartDate());
            response.setEndDate(request.getEndDate());
            response.setTotalDays(request.getTotalDays());
            response.setTotalPeople(request.getAdultsCount() + request.getChildrenCount());
            response.setStatus("Generated");
            response.setCreatedAt(java.time.LocalDateTime.now());
            
            // Create days with places
            List<ItineraryResponseDTO.ItineraryDayDTO> days = new ArrayList<>();
            int placeIndex = 0;
            
            for (int day = 1; day <= request.getTotalDays(); day++) {
                ItineraryResponseDTO.ItineraryDayDTO dayDTO = new ItineraryResponseDTO.ItineraryDayDTO();
                dayDTO.setDayNumber(day);
                dayDTO.setDate(request.getStartDate().plusDays(day - 1));
                dayDTO.setStartTime(java.time.LocalTime.of(8, 0));
                dayDTO.setEndTime(java.time.LocalTime.of(18, 0));
                dayDTO.setStartLocation(request.getStartingLocation() != null ? request.getStartingLocation() : "Starting Point");
                dayDTO.setEndLocation(request.getStartingLocation() != null ? request.getStartingLocation() : "Starting Point");
                
                List<ItineraryResponseDTO.ItineraryPlaceDTO> dayPlaces = new ArrayList<>();
                
                // Add 2 places per day (if available)
                for (int i = 0; i < 2 && placeIndex < filteredPlaces.size(); i++) {
                    Place place = filteredPlaces.get(placeIndex++);
                    
                    ItineraryResponseDTO.ItineraryPlaceDTO placeDTO = new ItineraryResponseDTO.ItineraryPlaceDTO();
                    placeDTO.setPlaceId(place.getPlaceId());
                    placeDTO.setPlaceName(place.getName());
                    placeDTO.setCategory(place.getCategory());
                    placeDTO.setDistrict(place.getDistrict());
                    placeDTO.setDescription(place.getDescription());
                    placeDTO.setLatitude(place.getLatitude());
                    placeDTO.setLongitude(place.getLongitude());
                    placeDTO.setVisitOrder(i + 1);
                    placeDTO.setEstimatedVisitDurationHours(place.getEstimatedTimeToVisit() != null ? place.getEstimatedTimeToVisit() : 2.0);
                    placeDTO.setTotalEntryCost(500.0);
                    placeDTO.setArrivalTime(java.time.LocalTime.of(9 + i * 3, 0));
                    placeDTO.setDepartureTime(java.time.LocalTime.of(11 + i * 3, 0));
                    placeDTO.setTransportFromPrevious("Car");
                    placeDTO.setTransportCost(200.0);
                    placeDTO.setDistanceFromPreviousKm(15.0);
                    placeDTO.setTravelTimeFromPreviousHours(0.5);
                    placeDTO.setWeatherCondition("Sunny");
                    placeDTO.setTemperatureCelsius(28.0);
                    
                    dayPlaces.add(placeDTO);
                }
                
                dayDTO.setPlaces(dayPlaces);
                dayDTO.setTotalDistanceKm(30.0);
                dayDTO.setEstimatedTravelTimeHours(1.0);
                dayDTO.setDayBudget(2500.0);
                days.add(dayDTO);
            }
            
            response.setDays(days);
            response.setTotalEstimatedCost(request.getTotalDays() * 2500.0);
            
            return response;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate itinerary: " + e.getMessage(), e);
        }
    }
    
    private boolean matchesCategories(Place place, List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return true;
        }
        
        return categories.stream()
            .anyMatch(category -> place.getCategory() != null && 
                     place.getCategory().toLowerCase().contains(category.toLowerCase()));
    }
    
    /**
     * Stub methods for controller compatibility
     */
    public List<ItineraryResponseDTO> getUserItineraries(Long userId) {
        return new ArrayList<>();
    }
    
    public ItineraryResponseDTO getItineraryById(Long itineraryId, Long userId) {
        throw new RuntimeException("Itinerary not found");
    }
    
    public void deleteItinerary(Long itineraryId, Long userId) {
        // Stub implementation
    }
    
    public List<Map<String, Object>> generateRecommendations(Map<String, Object> preferences) {
        // Get user preferences
        String preferredRegion = (String) preferences.get("region");
        String preferredCategory = (String) preferences.get("category");
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

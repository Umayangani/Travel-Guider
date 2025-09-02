package com.travelguider.backend.service;

import com.travelguider.backend.dto.ItineraryRequestDTO;
import com.travelguider.backend.dto.ItineraryResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalTime;
import java.util.*;

@Service
public class MLItineraryService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String ML_API_BASE_URL = "http://localhost:5000/api/ml";
    
    public MLItineraryService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Generate ML-powered itinerary
     */
    public ItineraryResponseDTO generateMLItinerary(Long userId, ItineraryRequestDTO request) {
        try {
            System.out.println("🤖 Generating ML-powered itinerary...");
            
            // Check if ML service is available
            if (!isMLServiceAvailable()) {
                throw new RuntimeException("ML service is not available");
            }
            
            // Prepare request for ML API
            Map<String, Object> mlRequest = new HashMap<>();
            mlRequest.put("totalDays", request.getTotalDays());
            mlRequest.put("adultsCount", request.getAdultsCount());
            mlRequest.put("childrenCount", request.getChildrenCount());
            mlRequest.put("budgetRange", request.getBudgetRange() != null ? request.getBudgetRange() : "Moderate");
            mlRequest.put("preferredCategories", request.getPreferredCategories());
            mlRequest.put("transportPreference", request.getTransportPreference());
            
            System.out.println("📤 Sending request to ML API: " + mlRequest);
            
            // Call ML API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(mlRequest, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                ML_API_BASE_URL + "/generate-itinerary", 
                entity, 
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse ML response and convert to ItineraryResponseDTO
                return parseMLResponse(response.getBody(), request);
            } else {
                throw new RuntimeException("ML API returned error: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error calling ML API: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate ML itinerary: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if ML service is available
     */
    public boolean isMLServiceAvailable() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                ML_API_BASE_URL.replace("/api/ml", "") + "/health", 
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode healthData = objectMapper.readTree(response.getBody());
                boolean modelLoaded = healthData.get("model_loaded").asBoolean();
                System.out.println("🏥 ML Service health: " + (modelLoaded ? "Ready" : "Model not loaded"));
                return modelLoaded;
            }
            
        } catch (Exception e) {
            System.err.println("🚫 ML service not available: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Trigger ML model retraining
     */
    public boolean retrainMLModel() {
        try {
            System.out.println("🔄 Triggering ML model retraining...");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>("{}", headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                ML_API_BASE_URL + "/retrain", 
                entity, 
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode responseData = objectMapper.readTree(response.getBody());
                boolean success = responseData.get("success").asBoolean();
                System.out.println("🎯 ML model retraining " + (success ? "successful" : "failed"));
                return success;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error retraining ML model: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get ML model information
     */
    public Map<String, Object> getMLModelInfo() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                ML_API_BASE_URL + "/model-info", 
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode responseData = objectMapper.readTree(response.getBody());
                if (responseData.get("success").asBoolean()) {
                    return objectMapper.convertValue(
                        responseData.get("model_info"), 
                        Map.class
                    );
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error getting ML model info: " + e.getMessage());
        }
        
        return new HashMap<>();
    }
    
    /**
     * Parse ML API response and convert to ItineraryResponseDTO
     */
    private ItineraryResponseDTO parseMLResponse(String responseBody, ItineraryRequestDTO request) {
        try {
            JsonNode mlResponse = objectMapper.readTree(responseBody);
            
            if (!mlResponse.get("success").asBoolean()) {
                throw new RuntimeException("ML API returned error");
            }
            
            JsonNode itineraryData = mlResponse.get("itinerary");
            
            // Create response DTO
            ItineraryResponseDTO response = new ItineraryResponseDTO();
            response.setId(System.currentTimeMillis());
            response.setTitle("ML-Powered " + itineraryData.get("title").asText());
            response.setStartDate(request.getStartDate());
            response.setEndDate(request.getEndDate());
            response.setTotalDays(request.getTotalDays());
            response.setTotalPeople(request.getAdultsCount() + request.getChildrenCount());
            response.setStatus("Generated by ML Model");
            response.setCreatedAt(java.time.LocalDateTime.now());
            
            // Convert daily plans
            List<ItineraryResponseDTO.ItineraryDayDTO> days = new ArrayList<>();
            JsonNode dailyPlans = itineraryData.get("daily_plans");
            double totalCost = 0.0;
            
            for (JsonNode dayPlan : dailyPlans) {
                ItineraryResponseDTO.ItineraryDayDTO dayDTO = createDayFromMLData(
                    dayPlan, request, response.getTotalDays()
                );
                totalCost += dayDTO.getDayBudget();
                days.add(dayDTO);
            }
            
            response.setDays(days);
            response.setTotalEstimatedCost(totalCost);
            
            System.out.println("✅ Successfully parsed ML response with " + days.size() + " days");
            
            return response;
            
        } catch (Exception e) {
            System.err.println("❌ Error parsing ML response: " + e.getMessage());
            throw new RuntimeException("Failed to parse ML response", e);
        }
    }
    
    /**
     * Create day DTO from ML data
     */
    private ItineraryResponseDTO.ItineraryDayDTO createDayFromMLData(
            JsonNode dayPlan, ItineraryRequestDTO request, int totalDays) {
        
        ItineraryResponseDTO.ItineraryDayDTO dayDTO = new ItineraryResponseDTO.ItineraryDayDTO();
        
        int dayNumber = dayPlan.get("day").asInt();
        dayDTO.setDayNumber(dayNumber);
        dayDTO.setDate(request.getStartDate().plusDays(dayNumber - 1));
        dayDTO.setStartTime(LocalTime.of(8, 0));
        dayDTO.setEndTime(LocalTime.of(18, 0));
        dayDTO.setStartLocation(dayNumber == 1 ? "Colombo (ML Starting Point)" : "Previous Location");
        dayDTO.setEndLocation(dayNumber == totalDays ? "Colombo (ML Return)" : "Day End Location");
        
        List<ItineraryResponseDTO.ItineraryPlaceDTO> placeDTOs = new ArrayList<>();
        JsonNode places = dayPlan.get("places");
        
        double dayTotalDistance = 0.0;
        double dayTotalTime = 0.0;
        double dayBudget = 0.0;
        
        // Colombo coordinates for distance calculations
        double currentLat = 6.9271;
        double currentLon = 79.8612;
        
        int visitOrder = 1;
        for (JsonNode place : places) {
            ItineraryResponseDTO.ItineraryPlaceDTO placeDTO = createPlaceFromMLData(
                place, visitOrder++, currentLat, currentLon, request
            );
            
            dayTotalDistance += placeDTO.getDistanceFromPreviousKm();
            dayTotalTime += placeDTO.getTravelTimeFromPreviousHours() + 
                           placeDTO.getEstimatedVisitDurationHours();
            dayBudget += placeDTO.getTotalEntryCost() + placeDTO.getTransportCost();
            
            placeDTOs.add(placeDTO);
            
            // Update current position
            currentLat = placeDTO.getLatitude();
            currentLon = placeDTO.getLongitude();
        }
        
        // Add return trip to Colombo
        if (!places.isEmpty()) {
            double returnDistance = calculateDistance(currentLat, currentLon, 6.9271, 79.8612);
            double returnTime = calculateTravelTime(returnDistance, request.getTransportPreference());
            double returnCost = calculateTransportCost(returnDistance, request.getTransportPreference());
            
            dayTotalDistance += returnDistance;
            dayTotalTime += returnTime;
            dayBudget += returnCost;
        }
        
        dayDTO.setPlaces(placeDTOs);
        dayDTO.setTotalDistanceKm(dayTotalDistance);
        dayDTO.setEstimatedTravelTimeHours(dayTotalTime);
        dayDTO.setDayBudget(dayBudget);
        
        return dayDTO;
    }
    
    /**
     * Create place DTO from ML data
     */
    private ItineraryResponseDTO.ItineraryPlaceDTO createPlaceFromMLData(
            JsonNode place, int visitOrder, double currentLat, double currentLon, 
            ItineraryRequestDTO request) {
        
        ItineraryResponseDTO.ItineraryPlaceDTO placeDTO = new ItineraryResponseDTO.ItineraryPlaceDTO();
        
        // Basic place info
        placeDTO.setPlaceId(place.get("place_id").asText());
        placeDTO.setPlaceName(place.get("name").asText());
        placeDTO.setCategory(place.get("category").asText());
        placeDTO.setDistrict(place.get("district").asText());
        placeDTO.setDescription(place.get("description").asText());
        placeDTO.setLatitude(place.get("latitude").asDouble());
        placeDTO.setLongitude(place.get("longitude").asDouble());
        placeDTO.setVisitOrder(visitOrder);
        
        // Calculate distance and travel time
        double distanceKm = calculateDistance(currentLat, currentLon, 
                                            placeDTO.getLatitude(), placeDTO.getLongitude());
        double travelTimeHours = calculateTravelTime(distanceKm, request.getTransportPreference());
        
        placeDTO.setDistanceFromPreviousKm(distanceKm);
        placeDTO.setTravelTimeFromPreviousHours(travelTimeHours);
        placeDTO.setTransportFromPrevious(request.getTransportPreference() != null ? 
                                         request.getTransportPreference() : "Car");
        placeDTO.setTransportCost(calculateTransportCost(distanceKm, request.getTransportPreference()));
        
        // Visit duration
        double visitDuration = place.has("estimated_time_to_visit") ? 
                              place.get("estimated_time_to_visit").asDouble() : 2.0;
        placeDTO.setEstimatedVisitDurationHours(visitDuration);
        
        // Times (simplified calculation)
        placeDTO.setArrivalTime(LocalTime.of(9 + (visitOrder - 1) * 2, 0));
        placeDTO.setDepartureTime(LocalTime.of(9 + (visitOrder - 1) * 2 + (int)visitDuration, 0));
        
        // Costs
        placeDTO.setTotalEntryCost(500.0); // Default entry cost
        
        // Weather
        placeDTO.setWeatherCondition("Partly Cloudy");
        placeDTO.setTemperatureCelsius(27.0 + Math.random() * 6);
        
        return placeDTO;
    }
    
    // Helper methods for calculations
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return Math.round(R * c * 100.0) / 100.0;
    }
    
    private double calculateTravelTime(double distanceKm, String transportMode) {
        double speedKmH = switch (transportMode != null ? transportMode.toLowerCase() : "car") {
            case "bus" -> 40;
            case "train" -> 50;
            default -> 60;
        };
        
        return Math.round((distanceKm / speedKmH) * 100.0) / 100.0;
    }
    
    private double calculateTransportCost(double distanceKm, String transportMode) {
        double costPerKm = switch (transportMode != null ? transportMode.toLowerCase() : "car") {
            case "bus" -> 5;
            case "train" -> 3;
            default -> 15;
        };
        
        return Math.round(distanceKm * costPerKm);
    }
}

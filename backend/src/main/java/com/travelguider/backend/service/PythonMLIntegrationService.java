package com.travelguider.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PythonMLIntegrationService {
    
    @Value("${app.ml.api.url:http://localhost:5000}")
    private String mlApiUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public PythonMLIntegrationService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Check if Python ML service is available
     */
    public boolean isMLServiceAvailable() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                mlApiUrl + "/health", Map.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            System.out.println("⚠️ ML Service not available: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get optimized itinerary from Python ML service
     */
    public Map<String, Object> getOptimizedItinerary(String startLocation, String endLocation, 
                                                    int durationDays, String travelStyle, 
                                                    List<String> interests) {
        try {
            if (!isMLServiceAvailable()) {
                throw new RuntimeException("ML service not available");
            }
            
            // Prepare request data
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("startingLocation", startLocation);
            requestData.put("endingLocation", endLocation);
            requestData.put("duration", durationDays);
            requestData.put("travelStyle", travelStyle);
            requestData.put("interests", interests);
            
            // Create HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create HTTP entity
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);
            
            // Make API call
            ResponseEntity<Map> response = restTemplate.postForEntity(
                mlApiUrl + "/api/ml/optimize-itinerary", 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> result = response.getBody();
                System.out.println("✅ ML Service response received successfully");
                return result;
            } else {
                throw new RuntimeException("ML service returned error: " + response.getStatusCode());
            }
            
        } catch (ResourceAccessException e) {
            System.out.println("🔴 ML Service connection failed: " + e.getMessage());
            throw new RuntimeException("Failed to connect to ML service", e);
        } catch (Exception e) {
            System.out.println("🔴 ML Service error: " + e.getMessage());
            throw new RuntimeException("ML service error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get places by region using ML service
     */
    public Map<String, Object> getPlacesByRegion(String region, List<String> interests, int maxPlaces) {
        try {
            if (!isMLServiceAvailable()) {
                return null;
            }
            
            // Build query parameters
            StringBuilder urlBuilder = new StringBuilder(mlApiUrl + "/api/ml/places-by-region");
            urlBuilder.append("?region=").append(region);
            urlBuilder.append("&max_places=").append(maxPlaces);
            
            if (interests != null && !interests.isEmpty()) {
                urlBuilder.append("&interests=").append(String.join(",", interests));
            }
            
            ResponseEntity<Map> response = restTemplate.getForEntity(urlBuilder.toString(), Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
            
        } catch (Exception e) {
            System.out.println("⚠️ ML places-by-region error: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Optimize route using ML service
     */
    public Map<String, Object> optimizeRoute(List<Map<String, Object>> places) {
        try {
            if (!isMLServiceAvailable() || places == null || places.isEmpty()) {
                return null;
            }
            
            // Prepare request data
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("places", places);
            
            // Create HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create HTTP entity
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);
            
            // Make API call
            ResponseEntity<Map> response = restTemplate.postForEntity(
                mlApiUrl + "/api/ml/route-optimization", 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
            
        } catch (Exception e) {
            System.out.println("⚠️ ML route optimization error: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get ML analytics
     */
    public Map<String, Object> getMLAnalytics() {
        try {
            if (!isMLServiceAvailable()) {
                return null;
            }
            
            ResponseEntity<Map> response = restTemplate.getForEntity(
                mlApiUrl + "/api/ml/analytics", Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
            
        } catch (Exception e) {
            System.out.println("⚠️ ML analytics error: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Reload ML data
     */
    public boolean reloadMLData() {
        try {
            if (!isMLServiceAvailable()) {
                return false;
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>("{}", headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                mlApiUrl + "/api/ml/reload-data", 
                entity, 
                Map.class
            );
            
            return response.getStatusCode() == HttpStatus.OK;
            
        } catch (Exception e) {
            System.out.println("⚠️ ML data reload error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Convert place entity to ML format
     */
    public Map<String, Object> convertPlaceToMLFormat(com.travelguider.backend.entity.Place place) {
        Map<String, Object> mlPlace = new HashMap<>();
        
        mlPlace.put("Place", place.getName());
        mlPlace.put("Category", place.getCategory());
        mlPlace.put("Region", place.getRegion());
        mlPlace.put("District", place.getDistrict());
        mlPlace.put("Latitude", place.getLatitude());
        mlPlace.put("Longitude", place.getLongitude());
        mlPlace.put("Ticket_price", 500.0); // Default ticket price
        mlPlace.put("Eestimated_time_to_visit", place.getEstimatedTimeToVisit());
        mlPlace.put("Contact_no", "+94-000-000000"); // Default contact
        mlPlace.put("Description", place.getDescription());
        
        return mlPlace;
    }
    
    /**
     * Fallback method when ML service is not available
     */
    public Map<String, Object> getFallbackItinerary(String startLocation, int durationDays, 
                                                   List<String> interests) {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("success", false);
        fallback.put("error", "ML service not available - using fallback");
        fallback.put("fallback_mode", true);
        fallback.put("message", "Basic itinerary generated without ML optimization");
        
        return fallback;
    }
}

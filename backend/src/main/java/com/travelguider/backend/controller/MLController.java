package com.travelguider.backend.controller;

import com.travelguider.backend.service.MLItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ml")
@CrossOrigin(origins = "*")
public class MLController {
    
    @Autowired
    private MLItineraryService mlItineraryService;
    
    /**
     * Check ML service health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkMLHealth() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean isAvailable = mlItineraryService.isMLServiceAvailable();
            response.put("ml_service_available", isAvailable);
            response.put("status", isAvailable ? "healthy" : "unavailable");
            response.put("message", isAvailable ? "ML service is ready" : "ML service is not available");
            
            if (isAvailable) {
                Map<String, Object> modelInfo = mlItineraryService.getMLModelInfo();
                response.put("model_info", modelInfo);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("ml_service_available", false);
            response.put("status", "error");
            response.put("message", "Error checking ML service: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * Trigger ML model retraining
     */
    @PostMapping("/retrain")
    public ResponseEntity<Map<String, Object>> retrainModel() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = mlItineraryService.retrainMLModel();
            
            response.put("success", success);
            response.put("message", success ? 
                "ML model retraining initiated successfully" : 
                "Failed to initiate ML model retraining");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retraining model: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * Get ML model information
     */
    @GetMapping("/model-info")
    public ResponseEntity<Map<String, Object>> getModelInfo() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (mlItineraryService.isMLServiceAvailable()) {
                Map<String, Object> modelInfo = mlItineraryService.getMLModelInfo();
                response.put("success", true);
                response.put("model_info", modelInfo);
            } else {
                response.put("success", false);
                response.put("message", "ML service not available");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error getting model info: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * Force ML-only itinerary generation (for testing)
     */
    @PostMapping("/force-ml-itinerary")
    public ResponseEntity<Map<String, Object>> forceMLItinerary(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // This endpoint forces ML generation even if rule-based is available
            // Useful for testing ML functionality
            
            response.put("success", false);
            response.put("message", "Direct ML itinerary endpoint not implemented yet. Use /api/itinerary/generate");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}

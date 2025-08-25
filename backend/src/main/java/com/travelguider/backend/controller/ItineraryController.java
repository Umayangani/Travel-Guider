package com.travelguider.backend.controller;

import com.travelguider.backend.service.ItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/itinerary")
@CrossOrigin(origins = "*")
public class ItineraryController {
    
    @Autowired
    private ItineraryService itineraryService;
    
    @PostMapping("/recommend")
    public ResponseEntity<?> recommendItinerary(@RequestBody Map<String, Object> preferences) {
        try {
            List<Map<String, Object>> recommendations = itineraryService.generateRecommendations(preferences);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "recommendations", recommendations
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to generate recommendations: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/destinations")
    public ResponseEntity<?> getAvailableDestinations() {
        try {
            List<Map<String, Object>> destinations = itineraryService.getAvailableDestinations();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "destinations", destinations
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to fetch destinations: " + e.getMessage()
            ));
        }
    }
}

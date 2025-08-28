package com.travelguider.backend.controller;

import com.travelguider.backend.dto.ItineraryRequestDTO;
import com.travelguider.backend.dto.ItineraryResponseDTO;
import com.travelguider.backend.service.ItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/itinerary")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ItineraryController {
    
    @Autowired
    private ItineraryService itineraryService;
    
    /**
     * Generate a new itinerary based on user preferences
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateItinerary(@RequestBody ItineraryRequestDTO request) {
        try {
            System.out.println("📥 Received request: " + request);
            System.out.println("📥 PreferredCategories type: " + (request.getPreferredCategories() != null ? request.getPreferredCategories().getClass() : "null"));
            System.out.println("📥 PreferredCategories value: " + request.getPreferredCategories());
            
            // Use default userId = 1 for testing
            Long userId = 1L;
            ItineraryResponseDTO itinerary = itineraryService.generateItinerary(userId, request);
            return ResponseEntity.ok(itinerary);
        } catch (Exception e) {
            System.err.println("❌ Error generating itinerary: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all itineraries for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserItineraries(@PathVariable Long userId) {
        try {
            List<ItineraryResponseDTO> itineraries = itineraryService.getUserItineraries(userId);
            return ResponseEntity.ok(itineraries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get a specific itinerary by ID
     */
    @GetMapping("/{itineraryId}")
    public ResponseEntity<?> getItinerary(@PathVariable Long itineraryId, 
                                        @RequestParam Long userId) {
        try {
            ItineraryResponseDTO itinerary = itineraryService.getItineraryById(itineraryId, userId);
            return ResponseEntity.ok(itinerary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete an itinerary
     */
    @DeleteMapping("/{itineraryId}")
    public ResponseEntity<?> deleteItinerary(@PathVariable Long itineraryId, 
                                           @RequestParam Long userId) {
        try {
            itineraryService.deleteItinerary(itineraryId, userId);
            return ResponseEntity.ok(Map.of("message", "Itinerary deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get available categories for preferences
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAvailableCategories() {
        List<String> categories = List.of(
            "Beach", "Cultural", "Religious", "Nature", "Adventure", 
            "Historical", "Wildlife", "Mountain", "Waterfall", "Museum", 
            "Park", "Temple", "Fort", "Cave", "Garden"
        );
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Get available districts for location selection
     */
    @GetMapping("/districts")
    public ResponseEntity<List<String>> getAvailableDistricts() {
        List<String> districts = List.of(
            "Colombo", "Gampaha", "Kalutara", "Kandy", "Matale", "Nuwara Eliya",
            "Galle", "Matara", "Hambantota", "Jaffna", "Kilinochchi", "Mannar",
            "Vavuniya", "Mullaitivu", "Batticaloa", "Ampara", "Trincomalee",
            "Kurunegala", "Puttalam", "Anuradhapura", "Polonnaruwa", "Badulla",
            "Moneragala", "Ratnapura", "Kegalle"
        );
        return ResponseEntity.ok(districts);
    }
    
    /**
     * Test endpoint to check if the controller is working
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testController() {
        return ResponseEntity.ok(Map.of(
            "message", "Itinerary Controller is working",
            "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }
}

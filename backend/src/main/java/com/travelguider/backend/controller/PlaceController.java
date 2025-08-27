package com.travelguider.backend.controller;

import com.travelguider.backend.entity.Place;
import com.travelguider.backend.entity.PlaceEntryFee;
import com.travelguider.backend.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(
    origins = {"http://localhost:3000", "http://localhost:3001"},
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
    allowCredentials = "true"
)
public class PlaceController {
    @Autowired
    private PlaceService placeService;

    @GetMapping("")
    public List<Place> getAllPlaces() {
        return placeService.getAllPlaces();
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<Place> getPlaceById(@PathVariable String placeId) {
        Optional<Place> place = placeService.getPlaceById(placeId);
        return place.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<Place> addPlace(@RequestBody PlaceWithFeeRequest request) {
        Place place = request.place;
        PlaceEntryFee entryFee = request.entryFee;
        Place saved = placeService.addPlace(place, entryFee);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{placeId}")
    public ResponseEntity<?> updatePlace(@PathVariable String placeId, @RequestBody Place place) {
        try {
            place.setPlaceId(placeId);
            Place updated = placeService.updatePlace(place);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{placeId}")
    public ResponseEntity<?> deletePlace(@PathVariable String placeId) {
        try {
            placeService.deletePlace(placeId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DTO for addPlace
    public static class PlaceWithFeeRequest {
        public Place place;
        public PlaceEntryFee entryFee;
    }
}

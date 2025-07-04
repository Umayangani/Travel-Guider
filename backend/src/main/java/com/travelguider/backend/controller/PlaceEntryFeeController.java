package com.travelguider.backend.controller;

import com.travelguider.backend.entity.PlaceEntryFee;
import com.travelguider.backend.repository.PlaceEntryFeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/entryfees")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class PlaceEntryFeeController {
    @Autowired
    private PlaceEntryFeeRepository placeEntryFeeRepository;

    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceEntryFee> getEntryFeeByPlaceId(@PathVariable String placeId) {
        PlaceEntryFee fee = placeEntryFeeRepository.findFirstByPlaceId(placeId);
        if (fee != null) {
            return ResponseEntity.ok(fee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{feeId}")
    public ResponseEntity<PlaceEntryFee> updateEntryFee(@PathVariable String feeId, @RequestBody PlaceEntryFee entryFee) {
        entryFee.setFeeId(feeId);
        PlaceEntryFee updated = placeEntryFeeRepository.save(entryFee);
        return ResponseEntity.ok(updated);
    }
}

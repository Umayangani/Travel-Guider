package com.travelguider.backend.controller;

import com.travelguider.backend.entity.PlaceEntryFee;
import com.travelguider.backend.service.EntryFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/entry-fees")
@CrossOrigin(
    origins = {"http://localhost:3000", "http://localhost:3001"},
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
    allowCredentials = "true"
)
public class EntryFeeController {

    @Autowired
    private EntryFeeService entryFeeService;

    @GetMapping("")
    public List<PlaceEntryFee> getAllEntryFees() {
        return entryFeeService.getAllEntryFees();
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceEntryFee> getEntryFeeByPlaceId(@PathVariable String placeId) {
        Optional<PlaceEntryFee> entryFee = entryFeeService.getEntryFeeByPlaceId(placeId);
        return entryFee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<PlaceEntryFee> createEntryFee(@RequestBody PlaceEntryFee entryFee) {
        PlaceEntryFee saved = entryFeeService.saveEntryFee(entryFee);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{placeId}")
    public ResponseEntity<PlaceEntryFee> updateEntryFee(@PathVariable String placeId, @RequestBody PlaceEntryFee entryFee) {
        try {
            entryFee.setPlaceId(placeId);
            PlaceEntryFee updated = entryFeeService.updateEntryFee(entryFee);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{placeId}")
    public ResponseEntity<?> deleteEntryFee(@PathVariable String placeId) {
        try {
            entryFeeService.deleteEntryFeeByPlaceId(placeId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

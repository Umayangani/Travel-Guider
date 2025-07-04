package com.travelguider.backend.controller;

import com.opencsv.exceptions.CsvValidationException;
import com.travelguider.backend.service.CsvImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/import")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class CsvImportController {
    @Autowired
    private CsvImportService csvImportService;

    @PostMapping("/places")
    public ResponseEntity<?> importPlaces(@RequestParam String path) {
        try {
            csvImportService.importPlaces(path);
            return ResponseEntity.ok("Places imported!");
        } catch (IOException | CsvValidationException e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/entryfees")
    public ResponseEntity<?> importEntryFees(@RequestParam String path) {
        try {
            csvImportService.importEntryFees(path);
            return ResponseEntity.ok("Entry fees imported!");
        } catch (IOException | CsvValidationException e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}

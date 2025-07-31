package com.travelguider.backend.controller;

import com.opencsv.exceptions.CsvValidationException;
import com.travelguider.backend.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/csv")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class CsvController {

    @Autowired
    private CsvService csvService;

    /**
     * Export places from database to CSV file
     */
    @PostMapping("/export")
    public ResponseEntity<Map<String, String>> exportPlaces() {
        try {
            String filePath = csvService.exportPlacesToCsv();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Places exported successfully");
            response.put("filePath", filePath);
            response.put("info", csvService.getCsvFileInfo());
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to export places: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Export places from database to ML-ready CSV file with additional features
     */
    @PostMapping("/export-ml")
    public ResponseEntity<Map<String, String>> exportPlacesForML() {
        try {
            String filePath = csvService.exportPlacesToCsvForML();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Places exported for ML successfully");
            response.put("filePath", filePath);
            response.put("fileName", "places_ml_ready.csv");
            response.put("features", "Includes encoded categories, computed features, and popularity scores");
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to export ML data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Upload CSV file to backend uploads folder
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Please select a file to upload");
                return ResponseEntity.badRequest().body(error);
            }

            String filePath = csvService.uploadCsvFile(file);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "CSV file uploaded successfully");
            response.put("filePath", filePath);
            response.put("originalName", file.getOriginalFilename());
            response.put("size", String.valueOf(file.getSize()));
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Import places from existing CSV file to database
     */
    @PostMapping("/import")
    public ResponseEntity<Map<String, String>> importPlaces() {
        try {
            csvService.importPlacesFromCsv();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Places imported successfully from CSV file");
            response.put("info", csvService.getCsvFileInfo());
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "File error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (CsvValidationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "CSV validation error: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Upload CSV file and immediately import to database
     */
    @PostMapping("/upload-and-import")
    public ResponseEntity<Map<String, String>> uploadAndImportCsv(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Please select a file to upload");
                return ResponseEntity.badRequest().body(error);
            }

            csvService.importPlacesFromUploadedCsv(file);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "CSV file uploaded and places imported successfully");
            response.put("originalName", file.getOriginalFilename());
            response.put("size", String.valueOf(file.getSize()));
            response.put("info", csvService.getCsvFileInfo());
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "File error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (CsvValidationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "CSV validation error: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Download CSV file
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadCsv() {
        try {
            File csvFile = csvService.getCsvFile();
            
            if (!csvFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(csvFile);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"places.csv\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .contentLength(csvFile.length())
                    .body(resource);
                    
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Download ML-ready CSV file
     */
    @GetMapping("/download-ml")
    public ResponseEntity<Resource> downloadMLCsv() {
        try {
            Path uploadPath = Paths.get("uploads");
            Path csvFilePath = uploadPath.resolve("places_ml_ready.csv");
            File csvFile = csvFilePath.toFile();
            
            if (!csvFile.exists()) {
                // Generate ML CSV if it doesn't exist
                csvService.exportPlacesToCsvForML();
            }

            Resource resource = new FileSystemResource(csvFile);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"places_ml_ready.csv\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .contentLength(csvFile.length())
                    .body(resource);
                    
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get CSV file status and info
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getCsvStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("exists", csvService.csvFileExists());
            
            if (csvService.csvFileExists()) {
                status.put("info", csvService.getCsvFileInfo());
            } else {
                status.put("info", "No CSV file found");
            }
            
            return ResponseEntity.ok(status);
        } catch (IOException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get CSV status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Delete CSV file
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteCsv() {
        try {
            File csvFile = csvService.getCsvFile();
            
            if (csvFile.exists() && csvFile.delete()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "CSV file deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "CSV file not found or could not be deleted");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete CSV file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

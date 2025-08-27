package com.travelguider.backend.controller;

import com.opencsv.exceptions.CsvValidationException;
import com.travelguider.backend.service.CsvService;
import com.travelguider.backend.service.EntryFeeCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/csv")
@CrossOrigin(
    origins = {"http://localhost:3000", "http://localhost:3001"},
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
    allowCredentials = "true"
)
public class CsvController {

    @Autowired
    private CsvService csvService;

    @Autowired
    private EntryFeeCsvService entryFeeCsvService;

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
            response.put("info", csvService.getCsvFileInfo("places"));
            
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
    @PostMapping("/upload/{type}")
    public ResponseEntity<Map<String, String>> uploadCsv(
            @RequestParam("file") MultipartFile file,
            @PathVariable("type") String type) {
        try {
            if (!type.equals("places") && !type.equals("entry-fees")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid type. Must be 'places' or 'entry-fees'");
                return ResponseEntity.badRequest().body(error);
            }

            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Please select a file to upload");
                return ResponseEntity.badRequest().body(error);
            }

            csvService.uploadCsvFile(file, type);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", String.format("%s CSV file uploaded successfully", type));
            response.put("type", type);
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
     * Import data from CSV to database
     */
    @PostMapping("/import/{type}")
    public ResponseEntity<Map<String, String>> importCsv(@PathVariable("type") String type) {
        try {
            if (!type.equals("places") && !type.equals("entry-fees")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid type. Must be 'places' or 'entry-fees'");
                return ResponseEntity.badRequest().body(error);
            }

            if (type.equals("places")) {
                csvService.importPlacesFromCsv();
            } else {
                entryFeeCsvService.importEntryFeesFromCsv();
            }
            
            Map<String, String> response = new HashMap<>();
            response.put("message", String.format("%s imported successfully", type));
            response.put("info", type.equals("places") ? 
                csvService.getCsvFileInfo(type) : 
                entryFeeCsvService.getCsvFileInfo());
            
            return ResponseEntity.ok(response);
        } catch (IOException | CsvValidationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to import " + type + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Upload and import CSV file in one step
     */
    @PostMapping("/upload-and-import/{type}")
    public ResponseEntity<Map<String, String>> uploadAndImportCsv(
            @RequestParam("file") MultipartFile file,
            @PathVariable("type") String type) {
        try {
            if (!type.equals("places") && !type.equals("entry-fees")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid type. Must be 'places' or 'entry-fees'");
                return ResponseEntity.badRequest().body(error);
            }

            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Please select a file to upload");
                return ResponseEntity.badRequest().body(error);
            }

            // Upload file first
            csvService.uploadCsvFile(file, type);
            
            // Then import the data
            if (type.equals("places")) {
                csvService.importPlacesFromUploadedCsv(file);
            } else {
                entryFeeCsvService.importEntryFeesFromUploadedCsv(file);
            }
            
            Map<String, String> response = new HashMap<>();
            response.put("message", String.format("%s file uploaded and imported successfully", type));
            response.put("type", type);
            response.put("originalName", file.getOriginalFilename());
            response.put("size", String.valueOf(file.getSize()));
            response.put("info", type.equals("places") ?
                csvService.getCsvFileInfo("places") :
                entryFeeCsvService.getCsvFileInfo());
            
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
    @GetMapping("/download/{type}")
    public ResponseEntity<Resource> downloadCsv(@PathVariable("type") String type) {
        try {
            if (!type.equals("places") && !type.equals("entry-fees")) {
                return ResponseEntity.badRequest().build();
            }
            
            File csvFile = type.equals("places") ? 
                csvService.getCsvFile("places") : 
                entryFeeCsvService.getCsvFile();
            
            if (!csvFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(csvFile);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                        String.format("attachment; filename=\"%s\"", 
                            type.equals("places") ? "places.csv" : "entry_fees.csv"))
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
            File csvFile = csvService.getCsvFile("places");
            
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
    @GetMapping("/status/{type}")
    public ResponseEntity<Map<String, Object>> getCsvStatus(@PathVariable("type") String type) {
        try {
            if (!type.equals("places") && !type.equals("entry-fees")) {
                return ResponseEntity.badRequest().build();
            }

            Map<String, Object> status = new HashMap<>();
            boolean exists = type.equals("places") ? 
                csvService.csvFileExists("places") : 
                entryFeeCsvService.csvFileExists();
            
            status.put("exists", exists);
            
            if (exists) {
                status.put("info", type.equals("places") ? 
                    csvService.getCsvFileInfo("places") :
                    entryFeeCsvService.getCsvFileInfo());
            } else {
                status.put("info", String.format("No %s CSV file found", type));
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
    @DeleteMapping("/delete/{type}")
    public ResponseEntity<Map<String, String>> deleteCsv(@PathVariable("type") String type) {
        try {
            if (!type.equals("places") && !type.equals("entry-fees")) {
                return ResponseEntity.badRequest().build();
            }
            
            File csvFile = type.equals("places") ? 
                csvService.getCsvFile("places") : 
                entryFeeCsvService.getCsvFile();
            
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

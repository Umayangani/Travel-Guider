package com.travelguider.backend.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.travelguider.backend.entity.Place;
import com.travelguider.backend.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class CsvService {

    @Autowired
    private PlaceRepository placeRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private static final String CSV_FILE_NAME = "places.csv";

    /**
     * Export all places from database to CSV file in uploads folder
     */
    public String exportPlacesToCsv() throws IOException {
        return exportPlacesToCsv("places.csv", false);
    }

    /**
     * Export places to CSV with ML-ready format
     */
    public String exportPlacesToCsvForML() throws IOException {
        return exportPlacesToCsv("places_ml_ready.csv", true);
    }

    /**
     * Export places to CSV with optional ML preprocessing
     */
    private String exportPlacesToCsv(String fileName, boolean mlFormat) throws IOException {
        // Create uploads directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Full path for CSV file
        Path csvFilePath = uploadPath.resolve(fileName);
        
        // Get all places from database
        List<Place> places = placeRepository.findAll();

        // Write to CSV
        try (FileWriter fileWriter = new FileWriter(csvFilePath.toFile());
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            if (mlFormat) {
                // ML-ready header with additional computed features
                String[] header = {
                    "place_id", "name", "district", "description", "region", 
                    "category", "estimated_time_to_visit", "latitude", "longitude",
                    // Additional ML features
                    "name_length", "description_length", "description_word_count",
                    "category_encoded", "region_encoded", "district_encoded",
                    "time_category", "location_zone", "popularity_score"
                };
                csvWriter.writeNext(header);

                // Write data rows with ML features
                for (Place place : places) {
                    String[] row = {
                        place.getPlaceId() != null ? place.getPlaceId() : "",
                        place.getName() != null ? place.getName() : "",
                        place.getDistrict() != null ? place.getDistrict() : "",
                        place.getDescription() != null ? place.getDescription() : "",
                        place.getRegion() != null ? place.getRegion() : "",
                        place.getCategory() != null ? place.getCategory() : "",
                        place.getEstimatedTimeToVisit() != null ? place.getEstimatedTimeToVisit().toString() : "",
                        place.getLatitude() != null ? place.getLatitude().toString() : "",
                        place.getLongitude() != null ? place.getLongitude().toString() : "",
                        
                        // ML Features
                        String.valueOf(place.getName() != null ? place.getName().length() : 0),
                        String.valueOf(place.getDescription() != null ? place.getDescription().length() : 0),
                        String.valueOf(place.getDescription() != null ? place.getDescription().split("\\s+").length : 0),
                        String.valueOf(encodeCategoryForML(place.getCategory())),
                        String.valueOf(encodeRegionForML(place.getRegion())),
                        String.valueOf(encodeDistrictForML(place.getDistrict())),
                        categorizeTimeForML(place.getEstimatedTimeToVisit()),
                        determineLocationZone(place.getLatitude(), place.getLongitude()),
                        String.valueOf(calculatePopularityScore(place))
                    };
                    csvWriter.writeNext(row);
                }
            } else {
                // Standard header
                String[] header = {
                    "place_id", "name", "district", "description", "region", 
                    "category", "estimated_time_to_visit", "latitude", "longitude"
                };
                csvWriter.writeNext(header);

                // Write standard data rows
                for (Place place : places) {
                    String[] row = {
                        place.getPlaceId() != null ? place.getPlaceId() : "",
                        place.getName() != null ? place.getName() : "",
                        place.getDistrict() != null ? place.getDistrict() : "",
                        place.getDescription() != null ? place.getDescription() : "",
                        place.getRegion() != null ? place.getRegion() : "",
                        place.getCategory() != null ? place.getCategory() : "",
                        place.getEstimatedTimeToVisit() != null ? place.getEstimatedTimeToVisit().toString() : "",
                        place.getLatitude() != null ? place.getLatitude().toString() : "",
                        place.getLongitude() != null ? place.getLongitude().toString() : ""
                    };
                    csvWriter.writeNext(row);
                }
            }
        }

        return csvFilePath.toString();
    }

    // ML Feature Engineering Helper Methods
    private int encodeCategoryForML(String category) {
        if (category == null) return 0;
        switch (category.toLowerCase()) {
            case "religious": return 1;
            case "natural": return 2;
            case "historical": return 3;
            case "cultural": return 4;
            case "adventure": return 5;
            case "beach": return 6;
            case "wildlife": return 7;
            default: return 0;
        }
    }

    private int encodeRegionForML(String region) {
        if (region == null) return 0;
        switch (region.toLowerCase()) {
            case "western": return 1;
            case "central": return 2;
            case "southern": return 3;
            case "northern": return 4;
            case "eastern": return 5;
            case "north western": return 6;
            case "north central": return 7;
            case "uva": return 8;
            case "sabaragamuwa": return 9;
            default: return 0;
        }
    }

    private int encodeDistrictForML(String district) {
        if (district == null) return 0;
        // Simple hash-based encoding for districts
        return Math.abs(district.hashCode()) % 26; // 26 districts in Sri Lanka
    }

    private String categorizeTimeForML(Double timeToVisit) {
        if (timeToVisit == null) return "unknown";
        if (timeToVisit <= 1) return "short";
        else if (timeToVisit <= 3) return "medium";
        else if (timeToVisit <= 6) return "long";
        else return "full_day";
    }

    private String determineLocationZone(Double lat, Double lng) {
        if (lat == null || lng == null) return "unknown";
        
        // Simple zoning based on Sri Lankan geography
        if (lat > 8.0) return "north";
        else if (lat > 7.0) return "central";
        else if (lat > 6.0) return "south";
        else return "deep_south";
    }

    private double calculatePopularityScore(Place place) {
        // Simple popularity score based on various factors
        double score = 0.0;
        
        // Name length factor (shorter names might be more famous)
        if (place.getName() != null) {
            score += (20 - Math.min(place.getName().length(), 20)) * 0.1;
        }
        
        // Category popularity weights
        if (place.getCategory() != null) {
            switch (place.getCategory().toLowerCase()) {
                case "religious": score += 0.8; break;
                case "historical": score += 0.9; break;
                case "natural": score += 0.7; break;
                case "beach": score += 0.6; break;
                default: score += 0.5; break;
            }
        }
        
        // Time to visit factor (moderate time places might be more popular)
        if (place.getEstimatedTimeToVisit() != null) {
            double time = place.getEstimatedTimeToVisit();
            if (time >= 2 && time <= 4) score += 0.3;
        }
        
        return Math.round(score * 100.0) / 100.0;
    }

    /**
     * Upload and save CSV file to uploads folder
     */
    public String uploadCsvFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            throw new IOException("File must be a CSV file");
        }

        // Create uploads directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file as places.csv (replace existing)
        Path csvFilePath = uploadPath.resolve(CSV_FILE_NAME);
        Files.copy(file.getInputStream(), csvFilePath, StandardCopyOption.REPLACE_EXISTING);

        return csvFilePath.toString();
    }

    /**
     * Import places from CSV file to database (replace all existing data)
     */
    public void importPlacesFromCsv() throws IOException, CsvValidationException {
        Path csvFilePath = Paths.get(uploadDir, CSV_FILE_NAME);
        
        if (!Files.exists(csvFilePath)) {
            throw new IOException("CSV file not found: " + csvFilePath);
        }

        // Clear existing places (optional - remove if you want to append instead)
        placeRepository.deleteAll();

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath.toFile()))) {
            String[] line;
            boolean isFirst = true;
            int rowNumber = 0;

            while ((line = reader.readNext()) != null) {
                rowNumber++;
                
                if (isFirst) { 
                    isFirst = false; 
                    continue; // skip header
                }

                // Validate minimum required columns
                if (line.length < 9) {
                    throw new CsvValidationException("Row " + rowNumber + ": Insufficient columns. Expected 9, got " + line.length);
                }

                try {
                    Place place = new Place();
                    place.setPlaceId(validateAndGet(line[0], "place_id", rowNumber));
                    place.setName(validateAndGet(line[1], "name", rowNumber));
                    place.setDistrict(line[2]);
                    place.setDescription(line[3]);
                    place.setRegion(line[4]);
                    place.setCategory(line[5]);
                    place.setEstimatedTimeToVisit(parseDouble(line[6], "estimated_time_to_visit", rowNumber));
                    place.setLatitude(parseDouble(line[7], "latitude", rowNumber));
                    place.setLongitude(parseDouble(line[8], "longitude", rowNumber));

                    placeRepository.save(place);
                } catch (Exception e) {
                    throw new CsvValidationException("Row " + rowNumber + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Import places from uploaded CSV file to database
     */
    public void importPlacesFromUploadedCsv(MultipartFile file) throws IOException, CsvValidationException {
        // First upload the file
        uploadCsvFile(file);
        
        // Then import from the uploaded file
        importPlacesFromCsv();
    }

    /**
     * Get the CSV file for download
     */
    public File getCsvFile() throws IOException {
        Path csvFilePath = Paths.get(uploadDir, CSV_FILE_NAME);
        
        if (!Files.exists(csvFilePath)) {
            // If file doesn't exist, export current data first
            exportPlacesToCsv();
        }
        
        return csvFilePath.toFile();
    }

    /**
     * Check if CSV file exists
     */
    public boolean csvFileExists() {
        Path csvFilePath = Paths.get(uploadDir, CSV_FILE_NAME);
        return Files.exists(csvFilePath);
    }

    /**
     * Get CSV file info
     */
    public String getCsvFileInfo() throws IOException {
        Path csvFilePath = Paths.get(uploadDir, CSV_FILE_NAME);
        
        if (!Files.exists(csvFilePath)) {
            return "CSV file does not exist";
        }

        long size = Files.size(csvFilePath);
        long lines = Files.lines(csvFilePath).count() - 1; // Subtract header
        
        return String.format("CSV file: %s, Size: %d bytes, Records: %d", 
                CSV_FILE_NAME, size, lines);
    }

    // Helper methods
    private String validateAndGet(String value, String fieldName, int rowNumber) throws CsvValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new CsvValidationException(fieldName + " is required");
        }
        return value.trim();
    }

    private Double parseDouble(String value, String fieldName, int rowNumber) throws CsvValidationException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new CsvValidationException(fieldName + " must be a valid number: " + value);
        }
    }
}

package com.travelguider.backend.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.travelguider.backend.entity.Place;
import com.travelguider.backend.entity.PlaceEntryFee;
import com.travelguider.backend.repository.PlaceRepository;
import com.travelguider.backend.repository.PlaceEntryFeeRepository;
import com.travelguider.backend.util.PlaceIdGenerator;
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

    @Autowired
    private PlaceEntryFeeRepository placeEntryFeeRepository;
    
    @Autowired
    private PlaceIdGenerator placeIdGenerator;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private static final String PLACES_CSV_FILE_NAME = "places.csv";
    private static final String ENTRY_FEES_CSV_FILE_NAME = "entry_fees.csv";

    /**
     * Export all places from database to CSV file in uploads folder
     */
    public String exportPlacesToCsv() throws IOException {
        return exportPlacesToCsv(PLACES_CSV_FILE_NAME, false);
    }

    /**
     * Export places to CSV with ML-ready format
     */
    public String exportPlacesToCsvForML() throws IOException {
        return exportPlacesToCsv("places_ml_ready.csv", true);
    }

    /**
     * Export entry fees to CSV
     */
    public String exportEntryFeesToCsv() throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path csvFilePath = uploadPath.resolve(ENTRY_FEES_CSV_FILE_NAME);
        
        List<PlaceEntryFee> entryFees = placeEntryFeeRepository.findAll();

        try (FileWriter fileWriter = new FileWriter(csvFilePath.toFile());
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            // Write header
            String[] header = {
                "place_id", "foreign_adult", "foreign_child", "local_adult", 
                "local_child", "student", "free_entry"
            };
            csvWriter.writeNext(header);

            // Write data rows
            for (PlaceEntryFee fee : entryFees) {
                String[] row = {
                    fee.getPlaceId(),
                    fee.getForeignAdult() != null ? fee.getForeignAdult().toString() : "",
                    fee.getForeignChild() != null ? fee.getForeignChild().toString() : "",
                    fee.getLocalAdult() != null ? fee.getLocalAdult().toString() : "",
                    fee.getLocalChild() != null ? fee.getLocalChild().toString() : "",
                    fee.getStudent() != null ? fee.getStudent().toString() : "",
                    fee.getFreeEntry() != null ? fee.getFreeEntry().toString() : "false"
                };
                csvWriter.writeNext(row);
            }
        }

        return csvFilePath.toString();
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
    public String uploadCsvFile(MultipartFile file, String type) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            throw new IOException("File must be a CSV file");
        }

        // Create uploads directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file based on type
        String targetFileName = type.equals("places") ? PLACES_CSV_FILE_NAME : ENTRY_FEES_CSV_FILE_NAME;
        Path csvFilePath = uploadPath.resolve(targetFileName);
        Files.copy(file.getInputStream(), csvFilePath, StandardCopyOption.REPLACE_EXISTING);

        return csvFilePath.toString();
    }

    /**
     * Import places and their entry fees from CSV file to database
     * CSV format should contain: Name, District, Description, Region, Category, 
     * EstimatedTimeToVisit, Latitude, Longitude, ForeignAdult, ForeignChild, LocalAdult, 
     * LocalChild, Student, FreeEntry
     */
    public void importPlacesFromCsv() throws IOException, CsvValidationException {
        Path csvFilePath = getCsvFilePath("places");
        
        if (!Files.exists(csvFilePath)) {
            throw new IOException("CSV file not found: " + csvFilePath);
        }

        try (CSVReader csvReader = new CSVReader(new FileReader(csvFilePath.toFile()))) {
            String[] headerRow = csvReader.readNext();
            if (headerRow == null) {
                throw new CsvValidationException("CSV file is empty");
            }

            // Define expected headers with possible variations
            String[][] expectedHeaders = {
                {"Name", "name", "PLACE_NAME", "place_name"},
                {"District", "district", "DISTRICT_NAME", "district_name"},
                {"Description", "description", "PLACE_DESC", "place_description"},
                {"Region", "region", "REGION_NAME", "region_name"},
                {"Category", "category", "PLACE_CATEGORY", "place_type"},
                {"Estimated_time_to_visit", "EstimatedTimeToVisit", "Estimated Time", "Time", "estimated_time", "time_to_visit", "visit_time", "VISIT_TIME", "TimeToVisit", "visittime", "ESTIMATED_TIME", "Eestimated_time_to_visit", "eestimated_time_to_visit"},
                {"Foreign_Adult", "ForeignAdult", "foreign_adult", "FOREIGN_ADULT", "foreignadult", "FOREIGN_ADULT_FEE", "foreign_fee_adult"},
                {"Foreign_Child", "ForeignChild", "foreign_child", "FOREIGN_CHILD", "foreignchild", "FOREIGN_CHILD_FEE", "foreign_fee_child"},
                {"Local_Adult", "LocalAdult", "local_adult", "LOCAL_ADULT", "localadult", "LOCAL_ADULT_FEE", "local_fee_adult"},
                {"Local_Child", "LocalChild", "local_child", "LOCAL_CHILD", "localchild", "LOCAL_CHILD_FEE", "local_fee_child"},
                {"Student", "student", "STUDENT_FEE", "student_fee", "STUDENT", "StudentFee"},
                {"Free_Entry", "FreeEntry", "free_entry", "FREE", "is_free", "FREE_ENTRY", "freeentry", "IS_FREE_ENTRY"},
                {"Latitude", "latitude", "LAT", "PLACE_LAT", "place_latitude"},
                {"Longitude", "longitude", "LNG", "LONG", "PLACE_LONG", "place_longitude"}
            };

            // Validate headers with variations
            for (String[] headerVariations : expectedHeaders) {
                boolean found = false;
                for (String header : headerRow) {
                    String cleanHeader = header.trim().replaceAll("[\\s_-]", "").toLowerCase();
                    for (String variation : headerVariations) {
                        if (cleanHeader.equals(variation.replaceAll("[\\s_-]", "").toLowerCase())) {
                            found = true;
                            break;
                        }
                    }
                    if (found) break;
                }
                if (!found) {
                    throw new CsvValidationException("Required column missing: " + headerVariations[0] + 
                        " (or variations like " + String.join(", ", headerVariations) + ")");
                }
            }

            // Get header indices
            int nameIdx = findColumnIndex(headerRow, "Name");
            int districtIdx = findColumnIndex(headerRow, "District");
            int descriptionIdx = findColumnIndex(headerRow, "Description");
            int regionIdx = findColumnIndex(headerRow, "Region");
            int categoryIdx = findColumnIndex(headerRow, "Category");
            int timeIdx = findColumnIndex(headerRow, "Estimated_time_to_visit");
            int foreignAdultIdx = findColumnIndex(headerRow, "Foreign_Adult");
            int foreignChildIdx = findColumnIndex(headerRow, "Foreign_Child");
            int localAdultIdx = findColumnIndex(headerRow, "Local_Adult");
            int localChildIdx = findColumnIndex(headerRow, "Local_Child");
            int studentIdx = findColumnIndex(headerRow, "Student");
            int freeEntryIdx = findColumnIndex(headerRow, "Free_Entry");
            int latitudeIdx = findColumnIndex(headerRow, "Latitude");
            int longitudeIdx = findColumnIndex(headerRow, "Longitude");

            int rowNumber = 1; // Start after header
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                rowNumber++;
                try {
                    Place place = new Place();
                    
                    // Get required fields and validate
                    String name = validateAndGet(row[nameIdx], "Name", rowNumber);
                    String district = validateAndGet(row[districtIdx], "District", rowNumber);
                    
                    // Generate unique ID based on district prefix
                    place.setPlaceId(placeIdGenerator.generatePlaceId(district));
                    
                    // Set basic info
                    place.setName(name);
                    place.setDistrict(district);
                    place.setDescription(row[descriptionIdx]);
                    place.setRegion(row[regionIdx]);
                    place.setCategory(row[categoryIdx]);
                    
                    // Set numeric fields with validation
                    if (row[timeIdx] != null && !row[timeIdx].trim().isEmpty()) {
                        place.setEstimatedTimeToVisit(parseDouble(row[timeIdx], "Estimated time", rowNumber));
                    }
                    
                    // Set coordinates with validation
                    if (row[latitudeIdx] != null && !row[latitudeIdx].trim().isEmpty()) {
                        Double lat = parseDouble(row[latitudeIdx], "Latitude", rowNumber);
                        if (lat < 5.0 || lat > 10.0) { // Sri Lanka's latitude range
                            throw new CsvValidationException("Invalid latitude value at row " + rowNumber + 
                                ". Must be between 5.0 and 10.0 for Sri Lanka");
                        }
                        place.setLatitude(lat);
                    }
                    
                    if (row[longitudeIdx] != null && !row[longitudeIdx].trim().isEmpty()) {
                        Double lng = parseDouble(row[longitudeIdx], "Longitude", rowNumber);
                        if (lng < 79.0 || lng > 82.0) { // Sri Lanka's longitude range
                            throw new CsvValidationException("Invalid longitude value at row " + rowNumber + 
                                ". Must be between 79.0 and 82.0 for Sri Lanka");
                        }
                        place.setLongitude(lng);
                    }
                    
                    // Save the place first to ensure it exists
                    Place savedPlace = placeRepository.save(place);
                    
                    // Create and save entry fee
                    PlaceEntryFee entryFee = new PlaceEntryFee();
                    entryFee.setFeeId(savedPlace.getPlaceId() + "-fee");
                    entryFee.setPlaceId(savedPlace.getPlaceId());
                    
                    // Set entry fees with validation
                    entryFee.setForeignAdult(parseDouble(row[foreignAdultIdx], "Foreign Adult fee", rowNumber));
                    entryFee.setForeignChild(parseDouble(row[foreignChildIdx], "Foreign Child fee", rowNumber));
                    entryFee.setLocalAdult(parseDouble(row[localAdultIdx], "Local Adult fee", rowNumber));
                    entryFee.setLocalChild(parseDouble(row[localChildIdx], "Local Child fee", rowNumber));
                    entryFee.setStudent(parseDouble(row[studentIdx], "Student fee", rowNumber));
                    
                    // Set free entry boolean
                    String freeEntryStr = row[freeEntryIdx].trim().toLowerCase();
                    boolean freeEntry = "yes".equals(freeEntryStr) || "true".equals(freeEntryStr) || "1".equals(freeEntryStr);
                    entryFee.setFreeEntry(freeEntry);
                    
                    // Validate fee consistency
                    if (freeEntry && (entryFee.getForeignAdult() != null || entryFee.getForeignChild() != null || 
                        entryFee.getLocalAdult() != null || entryFee.getLocalChild() != null || 
                        entryFee.getStudent() != null)) {
                        throw new CsvValidationException("Row " + rowNumber + ": Free entry places cannot have entry fees");
                    }
                    
                    if (!freeEntry && (entryFee.getForeignAdult() == null && entryFee.getForeignChild() == null && 
                        entryFee.getLocalAdult() == null && entryFee.getLocalChild() == null && 
                        entryFee.getStudent() == null)) {
                        throw new CsvValidationException("Row " + rowNumber + ": Non-free entry places must have at least one fee specified");
                    }
                    
                    // Save the entry fee
                    placeEntryFeeRepository.save(entryFee);
                    
                } catch (Exception e) {
                    throw new CsvValidationException("Error at row " + rowNumber + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Import places from uploaded CSV file to database
     */
    public void importPlacesFromUploadedCsv(MultipartFile file) throws IOException, CsvValidationException {
        // First upload the file
        uploadCsvFile(file, "places");
        
        // Then import from the uploaded file
        importPlacesFromCsv();
    }

    /**
     * Get the CSV file for download
     */
    public File getCsvFile(String type) throws IOException {
        Path csvFilePath = getCsvFilePath(type);
        
        if (!Files.exists(csvFilePath)) {
            // If file doesn't exist, export current data first
            exportPlacesToCsv();
        }
        
        return csvFilePath.toFile();
    }

    /**
     * Check if CSV file exists
     */
    public boolean csvFileExists(String type) {
        Path csvFilePath = getCsvFilePath(type);
        return Files.exists(csvFilePath);
    }

    /**
     * Get CSV file info
     */
    public String getCsvFileInfo(String type) throws IOException {
        Path csvFilePath = getCsvFilePath(type);
        
        if (!Files.exists(csvFilePath)) {
            return "CSV file does not exist";
        }

        long size = Files.size(csvFilePath);
        long lines = Files.lines(csvFilePath).count() - 1; // Subtract header
        
        String fileName = type.equals("places") ? PLACES_CSV_FILE_NAME : ENTRY_FEES_CSV_FILE_NAME;
        return String.format("CSV file: %s, Size: %d bytes, Records: %d", 
                fileName, size, lines);
    }

    // Helper methods
    private String validateAndGet(String value, String fieldName, int rowNumber) throws CsvValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new CsvValidationException(fieldName + " is required");
        }
        return value.trim();
    }

    /**
     * Find the index of a column in the header row
     */
    private int findColumnIndex(String[] headerRow, String columnName) throws CsvValidationException {
        String searchCol = columnName.replaceAll("[\\s_-]", "").toLowerCase();
        for (int i = 0; i < headerRow.length; i++) {
            String headerCol = headerRow[i].trim().replaceAll("[\\s_-]", "").toLowerCase();
            if (headerCol.equals(searchCol)) {
                return i;
            }
        }
        
        // If exact match not found, try partial match
        for (int i = 0; i < headerRow.length; i++) {
            String headerCol = headerRow[i].trim().replaceAll("[\\s_-]", "").toLowerCase();
            if (headerCol.contains(searchCol) || searchCol.contains(headerCol)) {
                return i;
            }
        }
        
        throw new CsvValidationException("Column not found: " + columnName + 
            ". Please make sure the CSV file has this column (or a similar variation).");
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

    /**
     * Get the file path for the given type
     */
    private Path getCsvFilePath(String type) {
        String fileName = type.equals("places") ? PLACES_CSV_FILE_NAME : ENTRY_FEES_CSV_FILE_NAME;
        return Paths.get(uploadDir, fileName);
    }
}

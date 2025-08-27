package com.travelguider.backend.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.travelguider.backend.entity.PlaceEntryFee;
import com.travelguider.backend.repository.PlaceEntryFeeRepository;
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
public class EntryFeeCsvService {

    @Autowired
    private PlaceEntryFeeRepository placeEntryFeeRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private static final String ENTRY_FEES_CSV_FILE_NAME = "entry_fees.csv";

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
     * Import entry fees from CSV
     */
    public void importEntryFeesFromCsv() throws IOException, CsvValidationException {
        Path csvFilePath = Paths.get(uploadDir, ENTRY_FEES_CSV_FILE_NAME);
        
        if (!Files.exists(csvFilePath)) {
            throw new IOException("Entry fees CSV file not found: " + csvFilePath);
        }

        try (CSVReader csvReader = new CSVReader(new FileReader(csvFilePath.toFile()))) {
            String[] headerRow = csvReader.readNext();
            if (headerRow == null) {
                throw new CsvValidationException("CSV file is empty");
            }

            // Get header indices
            int placeIdIdx = findColumnIndex(headerRow, "place_id");
            int foreignAdultIdx = findColumnIndex(headerRow, "foreign_adult");
            int foreignChildIdx = findColumnIndex(headerRow, "foreign_child");
            int localAdultIdx = findColumnIndex(headerRow, "local_adult");
            int localChildIdx = findColumnIndex(headerRow, "local_child");
            int studentIdx = findColumnIndex(headerRow, "student");
            int freeEntryIdx = findColumnIndex(headerRow, "free_entry");

            int rowNumber = 1;
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                rowNumber++;
                try {
                    PlaceEntryFee entryFee = new PlaceEntryFee();
                    
                    // Get required fields and validate
                    String placeId = validateAndGet(row[placeIdIdx], "Place ID", rowNumber);
                    entryFee.setPlaceId(placeId);
                    
                    // Set fee ID based on place ID
                    entryFee.setFeeId("FEE-" + placeId);
                    
                    // Parse boolean field
                    String freeEntryStr = row[freeEntryIdx].trim().toLowerCase();
                    boolean freeEntry = "yes".equals(freeEntryStr) || "true".equals(freeEntryStr) || "1".equals(freeEntryStr);
                    entryFee.setFreeEntry(freeEntry);
                    
                    // Set numeric fields only if not free entry
                    if (!freeEntry) {
                        if (row[foreignAdultIdx] != null && !row[foreignAdultIdx].trim().isEmpty()) {
                            entryFee.setForeignAdult(parseDouble(row[foreignAdultIdx], "Foreign Adult fee", rowNumber));
                        }
                        if (row[foreignChildIdx] != null && !row[foreignChildIdx].trim().isEmpty()) {
                            entryFee.setForeignChild(parseDouble(row[foreignChildIdx], "Foreign Child fee", rowNumber));
                        }
                        if (row[localAdultIdx] != null && !row[localAdultIdx].trim().isEmpty()) {
                            entryFee.setLocalAdult(parseDouble(row[localAdultIdx], "Local Adult fee", rowNumber));
                        }
                        if (row[localChildIdx] != null && !row[localChildIdx].trim().isEmpty()) {
                            entryFee.setLocalChild(parseDouble(row[localChildIdx], "Local Child fee", rowNumber));
                        }
                        if (row[studentIdx] != null && !row[studentIdx].trim().isEmpty()) {
                            entryFee.setStudent(parseDouble(row[studentIdx], "Student fee", rowNumber));
                        }
                        
                        // Validate that at least one fee is specified for non-free entry
                        if (entryFee.getForeignAdult() == null && entryFee.getForeignChild() == null &&
                            entryFee.getLocalAdult() == null && entryFee.getLocalChild() == null &&
                            entryFee.getStudent() == null) {
                            throw new CsvValidationException("Row " + rowNumber + ": Non-free entry places must have at least one fee specified");
                        }
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
     * Upload CSV file
     */
    public String uploadCsvFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(ENTRY_FEES_CSV_FILE_NAME);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return filePath.toString();
    }

    /**
     * Import entry fees from uploaded CSV file to database
     */
    public void importEntryFeesFromUploadedCsv(MultipartFile file) throws IOException, CsvValidationException {
        // First upload the file
        uploadCsvFile(file);
        
        // Then import from the uploaded file
        importEntryFeesFromCsv();
    }

    /**
     * Get CSV file info
     */
    public String getCsvFileInfo() throws IOException {
        Path csvFilePath = Paths.get(uploadDir, ENTRY_FEES_CSV_FILE_NAME);
        
        if (!Files.exists(csvFilePath)) {
            return "Entry fees CSV file does not exist";
        }

        long size = Files.size(csvFilePath);
        long lines = Files.lines(csvFilePath).count() - 1; // Subtract header
        
        return String.format("CSV file: %s, Size: %d bytes, Records: %d", 
                ENTRY_FEES_CSV_FILE_NAME, size, lines);
    }

    // Helper methods
    private String validateAndGet(String value, String fieldName, int rowNumber) throws CsvValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new CsvValidationException(fieldName + " is required at row " + rowNumber);
        }
        return value.trim();
    }

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
        
        throw new CsvValidationException("Column not found: " + columnName);
    }

    private Double parseDouble(String value, String fieldName, int rowNumber) throws CsvValidationException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new CsvValidationException(fieldName + " must be a valid number at row " + rowNumber + ": " + value);
        }
    }

    /**
     * Get CSV file for download
     */
    public File getCsvFile() throws IOException {
        Path csvFilePath = Paths.get(uploadDir, ENTRY_FEES_CSV_FILE_NAME);
        if (!Files.exists(csvFilePath)) {
            // If file doesn't exist, export current data first
            exportEntryFeesToCsv();
        }
        return csvFilePath.toFile();
    }

    /**
     * Check if CSV file exists
     */
    public boolean csvFileExists() {
        Path csvFilePath = Paths.get(uploadDir, ENTRY_FEES_CSV_FILE_NAME);
        return Files.exists(csvFilePath);
    }
}

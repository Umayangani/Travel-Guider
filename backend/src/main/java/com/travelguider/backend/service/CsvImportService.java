package com.travelguider.backend.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.travelguider.backend.entity.Place;
import com.travelguider.backend.entity.PlaceEntryFee;
import com.travelguider.backend.repository.PlaceRepository;
import com.travelguider.backend.repository.PlaceEntryFeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;

@Service
public class CsvImportService {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PlaceEntryFeeRepository placeEntryFeeRepository;

    public void importEntryFees(String csvFilePath) throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] line;
            boolean isFirst = true;
            while ((line = reader.readNext()) != null) {
                if (isFirst) { isFirst = false; continue; } // skip header
                // Generate fee_id if not present: place_id + "-FEE"
                String placeId = line[1];
                String feeId = placeId + "-FEE";
                PlaceEntryFee fee = new PlaceEntryFee();
                fee.setFeeId(feeId);
                fee.setPlaceId(placeId);
                fee.setForeignAdult(parseDecimal(line[3]));
                fee.setForeignChild(parseDecimal(line[4]));
                fee.setLocalAdult(parseDecimal(line[5]));
                fee.setLocalChild(parseDecimal(line[6]));
                fee.setStudent(parseDecimal(line[7]));
                fee.setFreeEntry("1".equals(line[8]) || "true".equalsIgnoreCase(line[8]));
                placeEntryFeeRepository.save(fee);
            }
        }
    }

    public void importPlaces(String csvFilePath) throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] line;
            boolean isFirst = true;
            while ((line = reader.readNext()) != null) {
                if (isFirst) { isFirst = false; continue; } // skip header
                // Generate place_id if not present: REGION-DISTRICT-XXX (XXX = 3-digit number, here we use from CSV)
                String placeId = line[0]; // Already in correct format in your CSV
                Place place = new Place();
                place.setPlaceId(placeId);
                place.setName(line[1]);
                place.setDistrict(line[2]);
                place.setDescription(line[3]);
                place.setRegion(line[4]);
                place.setCategory(line[5]);
                // Skipping entryFeeId (not present in entity)
                place.setEstimatedTimeToVisit(parseDouble(line[7]));
                place.setLatitude(parseDouble(line[8]));
                place.setLongitude(parseDouble(line[9]));
                placeRepository.save(place);
            }
        }
    }

    private Double parseDecimal(String value) {
        try {
            return value == null || value.isEmpty() ? 0.0 : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private Double parseDouble(String value) {
        try {
            return value == null || value.isEmpty() ? null : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

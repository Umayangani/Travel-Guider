package com.travelguider.backend.service;

import com.travelguider.backend.entity.PlaceEntryFee;
import com.travelguider.backend.repository.PlaceEntryFeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EntryFeeService {

    @Autowired
    private PlaceEntryFeeRepository entryFeeRepository;

    public List<PlaceEntryFee> getAllEntryFees() {
        return entryFeeRepository.findAll();
    }

    public Optional<PlaceEntryFee> getEntryFeeByPlaceId(String placeId) {
        PlaceEntryFee entryFee = entryFeeRepository.findFirstByPlaceId(placeId);
        return Optional.ofNullable(entryFee);
    }

    @Transactional
    public PlaceEntryFee saveEntryFee(PlaceEntryFee entryFee) {
        // Generate fee ID if not set
        if (entryFee.getFeeId() == null || entryFee.getFeeId().isEmpty()) {
            entryFee.setFeeId(entryFee.getPlaceId() + "-FEE");
        }
        return entryFeeRepository.save(entryFee);
    }

    @Transactional
    public PlaceEntryFee updateEntryFee(PlaceEntryFee entryFee) {
        PlaceEntryFee existing = entryFeeRepository.findFirstByPlaceId(entryFee.getPlaceId());
        if (existing == null) {
            // Create new if doesn't exist
            entryFee.setFeeId(entryFee.getPlaceId() + "-FEE");
            return entryFeeRepository.save(entryFee);
        } else {
            // Update existing
            existing.setForeignAdult(entryFee.getForeignAdult());
            existing.setForeignChild(entryFee.getForeignChild());
            existing.setLocalAdult(entryFee.getLocalAdult());
            existing.setLocalChild(entryFee.getLocalChild());
            existing.setStudent(entryFee.getStudent());
            existing.setFreeEntry(entryFee.getFreeEntry());
            return entryFeeRepository.save(existing);
        }
    }

    @Transactional
    public void deleteEntryFeeByPlaceId(String placeId) {
        PlaceEntryFee entryFee = entryFeeRepository.findFirstByPlaceId(placeId);
        if (entryFee != null) {
            entryFeeRepository.delete(entryFee);
        }
    }
}

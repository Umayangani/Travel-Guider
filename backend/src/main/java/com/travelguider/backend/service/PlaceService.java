package com.travelguider.backend.service;

import com.travelguider.backend.entity.Place;
import com.travelguider.backend.entity.PlaceEntryFee;
import com.travelguider.backend.repository.PlaceRepository;
import com.travelguider.backend.repository.PlaceEntryFeeRepository;
import com.travelguider.backend.util.PlaceIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceService {
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private PlaceEntryFeeRepository placeEntryFeeRepository;
    @Autowired
    private PlaceIdGenerator placeIdGenerator;
    @Autowired
    private AutoMLService autoMLService;

    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    public Optional<Place> getPlaceById(String placeId) {
        return placeRepository.findById(placeId);
    }

    @Transactional
    public Place addPlace(Place place, PlaceEntryFee entryFee) {
        // Generate place_id using PlaceIdGenerator
        String placeId = placeIdGenerator.generatePlaceId(place.getDistrict());
        place.setPlaceId(placeId);
        
        // Generate fee_id
        String feeId = placeIdGenerator.generateEntryFeeId(placeId);
        entryFee.setFeeId(feeId);
        entryFee.setPlaceId(placeId);
        
        // Save place first
        Place savedPlace = placeRepository.save(place);
        
        // Save entry fee
        placeEntryFeeRepository.save(entryFee);
        
        // Update ML datasets automatically
        autoMLService.updateMLDatasets();
        
        return savedPlace;
    }

    @Transactional
    public Place updatePlace(Place place) {
        Optional<Place> existingPlace = placeRepository.findById(place.getPlaceId());
        if (existingPlace.isEmpty()) {
            throw new RuntimeException("Place not found with ID: " + place.getPlaceId());
        }
        
        // Keep the original ID and update other fields
        Place existing = existingPlace.get();
        existing.setName(place.getName());
        existing.setDistrict(place.getDistrict());
        existing.setDescription(place.getDescription());
        existing.setRegion(place.getRegion());
        existing.setCategory(place.getCategory());
        existing.setEstimatedTimeToVisit(place.getEstimatedTimeToVisit());
        existing.setLatitude(place.getLatitude());
        existing.setLongitude(place.getLongitude());
        
        Place updated = placeRepository.save(existing);
        
        // Update ML datasets automatically
        autoMLService.updateMLDatasets();
        
        return updated;
    }

    @Transactional
    public void deletePlace(String placeId) {
        if (!placeRepository.existsById(placeId)) {
            throw new RuntimeException("Place not found with ID: " + placeId);
        }
        
        // Delete associated entry fees first
        PlaceEntryFee fee = placeEntryFeeRepository.findFirstByPlaceId(placeId);
        if (fee != null) {
            placeEntryFeeRepository.delete(fee);
        }
        
        // Then delete the place
        placeRepository.deleteById(placeId);
        
        // Update ML datasets automatically
        autoMLService.updateMLDatasets();
    }
}

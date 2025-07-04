package com.travelguider.backend.service;

import com.travelguider.backend.entity.Place;
import com.travelguider.backend.entity.PlaceEntryFee;
import com.travelguider.backend.repository.PlaceRepository;
import com.travelguider.backend.repository.PlaceEntryFeeRepository;
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

    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    public Optional<Place> getPlaceById(String placeId) {
        return placeRepository.findById(placeId);
    }

    @Transactional
    public Place addPlace(Place place, PlaceEntryFee entryFee) {
        // Generate place_id: REGION-DISTRICT-XXX
        String region = place.getRegion();
        String district = place.getDistrict();
        String prefix = region.substring(0, 2).toUpperCase() + "-" + district.substring(0, 3).toUpperCase();
        String lastPlaceId = placeRepository.findLastPlaceIdByPrefix(prefix + "%");
        int nextNumber = 1;
        if (lastPlaceId != null) {
            String[] parts = lastPlaceId.split("-");
            if (parts.length == 3) {
                try {
                    nextNumber = Integer.parseInt(parts[2]) + 1;
                } catch (NumberFormatException ignored) {}
            }
        }
        String placeId = prefix + "-" + String.format("%03d", nextNumber);
        place.setPlaceId(placeId);
        // Generate fee_id
        String feeId = placeId + "-FEE";
        entryFee.setFeeId(feeId);
        entryFee.setPlaceId(placeId);
        placeRepository.save(place);
        placeEntryFeeRepository.save(entryFee);
        return place;
    }

    @Transactional
    public Place updatePlace(Place place) {
        return placeRepository.save(place);
    }

    @Transactional
    public void deletePlace(String placeId) {
        placeEntryFeeRepository.deleteAll(placeEntryFeeRepository.findAll().stream().filter(f -> f.getPlaceId().equals(placeId)).toList());
        placeRepository.deleteById(placeId);
    }
}

package com.travelguider.backend.repository;

import com.travelguider.backend.entity.PlaceEntryFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceEntryFeeRepository extends JpaRepository<PlaceEntryFee, String> {
    PlaceEntryFee findFirstByPlaceId(String placeId);
}

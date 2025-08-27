package com.travelguider.backend.repository;

import com.travelguider.backend.entity.PlaceEntryFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlaceEntryFeeRepository extends JpaRepository<PlaceEntryFee, String> {
    PlaceEntryFee findFirstByPlaceId(String placeId);
    void deleteByPlaceId(String placeId);
    List<PlaceEntryFee> findByPlaceId(String placeId);
}

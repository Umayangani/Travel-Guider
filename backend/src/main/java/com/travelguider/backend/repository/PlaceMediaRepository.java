package com.travelguider.backend.repository;

import com.travelguider.backend.entity.PlaceMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaceMediaRepository extends JpaRepository<PlaceMedia, Long> {
    List<PlaceMedia> findByPlaceId(String placeId);
}

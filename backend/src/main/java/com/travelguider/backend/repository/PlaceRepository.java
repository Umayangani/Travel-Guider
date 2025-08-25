package com.travelguider.backend.repository;

import com.travelguider.backend.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, String> {
    @Query("SELECT p.placeId FROM Place p WHERE p.placeId LIKE :prefix ORDER BY p.placeId DESC LIMIT 1")
    String findLastPlaceIdByPrefix(@Param("prefix") String prefix);
}

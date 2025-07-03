package com.travelguider.backend.repository;

import com.travelguider.backend.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, String> {}

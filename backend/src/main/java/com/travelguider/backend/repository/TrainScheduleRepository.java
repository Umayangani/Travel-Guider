package com.travelguider.backend.repository;

import com.travelguider.backend.entity.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, String> {
    @Query("SELECT COUNT(ts) FROM TrainSchedule ts WHERE ts.fromStation = :fromStation AND ts.toStation = :toStation")
    long countByFromStationAndToStation(@Param("fromStation") String fromStation, @Param("toStation") String toStation);
}

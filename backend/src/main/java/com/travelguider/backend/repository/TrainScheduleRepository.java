package com.travelguider.backend.repository;

import com.travelguider.backend.entity.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, String> {
    @Query("SELECT COUNT(ts) FROM TrainSchedule ts WHERE ts.fromStation = :fromStation AND ts.toStation = :toStation")
    long countByFromStationAndToStation(@Param("fromStation") String fromStation, @Param("toStation") String toStation);
    
    @Query("SELECT ts FROM TrainSchedule ts WHERE " +
           "LOWER(ts.fromStation) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ts.toStation) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ts.trainName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<TrainSchedule> searchSchedules(@Param("keyword") String keyword);
    
    @Query("SELECT ts FROM TrainSchedule ts WHERE " +
           "LOWER(ts.fromStation) LIKE LOWER(CONCAT('%', :fromStation, '%')) AND " +
           "LOWER(ts.toStation) LIKE LOWER(CONCAT('%', :toStation, '%'))")
    List<TrainSchedule> findByStations(@Param("fromStation") String fromStation, @Param("toStation") String toStation);
}

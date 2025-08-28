package com.travelguider.backend.repository;

import com.travelguider.backend.entity.ItineraryDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItineraryDayRepository extends JpaRepository<ItineraryDay, Long> {
    
    List<ItineraryDay> findByItineraryIdOrderByDayNumber(Long itineraryId);
    
    Optional<ItineraryDay> findByItineraryIdAndDayNumber(Long itineraryId, Integer dayNumber);
    
    @Query("SELECT id FROM ItineraryDay id WHERE id.date = :date AND id.itinerary.userId = :userId")
    Optional<ItineraryDay> findByDateAndUserId(@Param("date") LocalDate date, @Param("userId") Long userId);
    
    @Query("SELECT id FROM ItineraryDay id WHERE id.itinerary.id = :itineraryId AND id.date BETWEEN :startDate AND :endDate")
    List<ItineraryDay> findByItineraryIdAndDateRange(@Param("itineraryId") Long itineraryId, 
                                                     @Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);
}

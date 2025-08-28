package com.travelguider.backend.repository;

import com.travelguider.backend.entity.ItineraryPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItineraryPlaceRepository extends JpaRepository<ItineraryPlace, Long> {
    
    List<ItineraryPlace> findByItineraryDayIdOrderByVisitOrder(Long itineraryDayId);
    
    List<ItineraryPlace> findByPlacePlaceId(String placeId);
    
    @Query("SELECT ip FROM ItineraryPlace ip WHERE ip.itineraryDay.itinerary.userId = :userId")
    List<ItineraryPlace> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT ip FROM ItineraryPlace ip WHERE ip.itineraryDay.id = :dayId AND ip.visitOrder = :order")
    ItineraryPlace findByDayIdAndVisitOrder(@Param("dayId") Long dayId, @Param("order") Integer order);
    
    @Query("SELECT COUNT(ip) FROM ItineraryPlace ip WHERE ip.place.placeId = :placeId")
    Long countByPlaceId(@Param("placeId") String placeId);
}

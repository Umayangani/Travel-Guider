package com.travelguider.backend.repository;

import com.travelguider.backend.entity.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    
    List<Itinerary> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Itinerary> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);
    
    @Query("SELECT i FROM Itinerary i WHERE i.userId = :userId AND i.startDate >= :startDate")
    List<Itinerary> findUpcomingItinerariesByUserId(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);
    
    @Query("SELECT i FROM Itinerary i WHERE i.userId = :userId AND i.endDate < :endDate")
    List<Itinerary> findPastItinerariesByUserId(@Param("userId") Long userId, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT i FROM Itinerary i WHERE i.startDate <= :date AND i.endDate >= :date AND i.userId = :userId")
    Optional<Itinerary> findCurrentItineraryByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(i) FROM Itinerary i WHERE i.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT i FROM Itinerary i WHERE i.preferredCategories LIKE %:category%")
    List<Itinerary> findByPreferredCategoriesContaining(@Param("category") String category);
}

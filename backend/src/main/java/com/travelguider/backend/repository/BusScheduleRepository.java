package com.travelguider.backend.repository;

import com.travelguider.backend.entity.BusSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BusScheduleRepository extends JpaRepository<BusSchedule, String> {
    @Query("SELECT COUNT(b) FROM BusSchedule b WHERE b.departureLocation = :from AND b.arrivalLocation = :to")
    long countByFromAndTo(@Param("from") String from, @Param("to") String to);
}

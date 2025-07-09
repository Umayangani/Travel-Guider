package com.travelguider.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "highway_bus_schedule")
public class BusSchedule implements Serializable {
    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "route_name", length = 255, nullable = false)
    private String routeName;

    @Column(name = "departure_location", length = 255, nullable = false)
    private String departureLocation;

    @Column(name = "arrival_location", length = 255, nullable = false)
    private String arrivalLocation;

    @Column(name = "departure_time", nullable = false)
    private java.sql.Time departureTime;

    @Column(name = "arrival_time", nullable = false)
    private java.sql.Time arrivalTime;

    @Column(name = "frequency", length = 20)
    private String frequency;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    public String getDepartureLocation() { return departureLocation; }
    public void setDepartureLocation(String departureLocation) { this.departureLocation = departureLocation; }
    public String getArrivalLocation() { return arrivalLocation; }
    public void setArrivalLocation(String arrivalLocation) { this.arrivalLocation = arrivalLocation; }
    public java.sql.Time getDepartureTime() { return departureTime; }
    public void setDepartureTime(java.sql.Time departureTime) { this.departureTime = departureTime; }
    public java.sql.Time getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(java.sql.Time arrivalTime) { this.arrivalTime = arrivalTime; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
}

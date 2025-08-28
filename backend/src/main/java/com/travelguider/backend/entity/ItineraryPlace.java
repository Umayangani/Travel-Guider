package com.travelguider.backend.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "itinerary_places")
public class ItineraryPlace {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_day_id", nullable = false)
    private ItineraryDay itineraryDay;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;
    
    @Column(name = "visit_order")
    private Integer visitOrder;
    
    @Column(name = "arrival_time")
    private LocalTime arrivalTime;
    
    @Column(name = "departure_time")
    private LocalTime departureTime;
    
    @Column(name = "estimated_visit_duration_hours")
    private Double estimatedVisitDurationHours;
    
    @Column(name = "entry_fee_adults")
    private Double entryFeeAdults;
    
    @Column(name = "entry_fee_children")
    private Double entryFeeChildren;
    
    @Column(name = "entry_fee_students")
    private Double entryFeeStudents;
    
    @Column(name = "entry_fee_foreigners")
    private Double entryFeeForeigners;
    
    @Column(name = "total_entry_cost")
    private Double totalEntryCost;
    
    @Column(name = "transport_from_previous")
    private String transportFromPrevious; // BUS, TRAIN, TAXI, WALK
    
    @Column(name = "transport_cost")
    private Double transportCost;
    
    @Column(name = "distance_from_previous_km")
    private Double distanceFromPreviousKm;
    
    @Column(name = "travel_time_from_previous_hours")
    private Double travelTimeFromPreviousHours;
    
    @Column(name = "weather_condition", length = 100)
    private String weatherCondition;
    
    @Column(name = "temperature_celsius")
    private Double temperatureCelsius;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    // Constructors
    public ItineraryPlace() {}
    
    public ItineraryPlace(ItineraryDay itineraryDay, Place place, Integer visitOrder) {
        this.itineraryDay = itineraryDay;
        this.place = place;
        this.visitOrder = visitOrder;
        this.estimatedVisitDurationHours = place.getEstimatedTimeToVisit();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public ItineraryDay getItineraryDay() { return itineraryDay; }
    public void setItineraryDay(ItineraryDay itineraryDay) { this.itineraryDay = itineraryDay; }
    
    public Place getPlace() { return place; }
    public void setPlace(Place place) { this.place = place; }
    
    public Integer getVisitOrder() { return visitOrder; }
    public void setVisitOrder(Integer visitOrder) { this.visitOrder = visitOrder; }
    
    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }
    
    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }
    
    public Double getEstimatedVisitDurationHours() { return estimatedVisitDurationHours; }
    public void setEstimatedVisitDurationHours(Double estimatedVisitDurationHours) { 
        this.estimatedVisitDurationHours = estimatedVisitDurationHours; 
    }
    
    public Double getEntryFeeAdults() { return entryFeeAdults; }
    public void setEntryFeeAdults(Double entryFeeAdults) { this.entryFeeAdults = entryFeeAdults; }
    
    public Double getEntryFeeChildren() { return entryFeeChildren; }
    public void setEntryFeeChildren(Double entryFeeChildren) { this.entryFeeChildren = entryFeeChildren; }
    
    public Double getEntryFeeStudents() { return entryFeeStudents; }
    public void setEntryFeeStudents(Double entryFeeStudents) { this.entryFeeStudents = entryFeeStudents; }
    
    public Double getEntryFeeForeigners() { return entryFeeForeigners; }
    public void setEntryFeeForeigners(Double entryFeeForeigners) { this.entryFeeForeigners = entryFeeForeigners; }
    
    public Double getTotalEntryCost() { return totalEntryCost; }
    public void setTotalEntryCost(Double totalEntryCost) { this.totalEntryCost = totalEntryCost; }
    
    public String getTransportFromPrevious() { return transportFromPrevious; }
    public void setTransportFromPrevious(String transportFromPrevious) { this.transportFromPrevious = transportFromPrevious; }
    
    public Double getTransportCost() { return transportCost; }
    public void setTransportCost(Double transportCost) { this.transportCost = transportCost; }
    
    public Double getDistanceFromPreviousKm() { return distanceFromPreviousKm; }
    public void setDistanceFromPreviousKm(Double distanceFromPreviousKm) { this.distanceFromPreviousKm = distanceFromPreviousKm; }
    
    public Double getTravelTimeFromPreviousHours() { return travelTimeFromPreviousHours; }
    public void setTravelTimeFromPreviousHours(Double travelTimeFromPreviousHours) { 
        this.travelTimeFromPreviousHours = travelTimeFromPreviousHours; 
    }
    
    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }
    
    public Double getTemperatureCelsius() { return temperatureCelsius; }
    public void setTemperatureCelsius(Double temperatureCelsius) { this.temperatureCelsius = temperatureCelsius; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

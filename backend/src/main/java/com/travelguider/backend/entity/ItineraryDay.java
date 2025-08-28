package com.travelguider.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "itinerary_days")
public class ItineraryDay {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id", nullable = false)
    private Itinerary itinerary;
    
    @Column(name = "day_number")
    private Integer dayNumber;
    
    @Column(name = "date")
    private LocalDate date;
    
    @Column(name = "start_time")
    private LocalTime startTime;
    
    @Column(name = "end_time")
    private LocalTime endTime;
    
    @Column(name = "start_location", length = 200)
    private String startLocation;
    
    @Column(name = "end_location", length = 200)
    private String endLocation;
    
    @Column(name = "total_distance_km")
    private Double totalDistanceKm;
    
    @Column(name = "estimated_travel_time_hours")
    private Double estimatedTravelTimeHours;
    
    @Column(name = "day_budget")
    private Double dayBudget;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @OneToMany(mappedBy = "itineraryDay", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItineraryPlace> places;
    
    // Constructors
    public ItineraryDay() {}
    
    public ItineraryDay(Itinerary itinerary, Integer dayNumber, LocalDate date) {
        this.itinerary = itinerary;
        this.dayNumber = dayNumber;
        this.date = date;
        this.startTime = LocalTime.of(6, 30); // Default start time
        this.endTime = LocalTime.of(18, 0);   // Default end time
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Itinerary getItinerary() { return itinerary; }
    public void setItinerary(Itinerary itinerary) { this.itinerary = itinerary; }
    
    public Integer getDayNumber() { return dayNumber; }
    public void setDayNumber(Integer dayNumber) { this.dayNumber = dayNumber; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    public String getStartLocation() { return startLocation; }
    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }
    
    public String getEndLocation() { return endLocation; }
    public void setEndLocation(String endLocation) { this.endLocation = endLocation; }
    
    public Double getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(Double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }
    
    public Double getEstimatedTravelTimeHours() { return estimatedTravelTimeHours; }
    public void setEstimatedTravelTimeHours(Double estimatedTravelTimeHours) { 
        this.estimatedTravelTimeHours = estimatedTravelTimeHours; 
    }
    
    public Double getDayBudget() { return dayBudget; }
    public void setDayBudget(Double dayBudget) { this.dayBudget = dayBudget; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public List<ItineraryPlace> getPlaces() { return places; }
    public void setPlaces(List<ItineraryPlace> places) { this.places = places; }
}

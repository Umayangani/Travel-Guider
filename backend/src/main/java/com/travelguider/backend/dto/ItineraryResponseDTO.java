package com.travelguider.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ItineraryResponseDTO {
    
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDays;
    private Integer totalPeople;
    private String status;
    private Double totalEstimatedCost;
    private LocalDateTime createdAt;
    private List<ItineraryDayDTO> days;
    
    // Inner class for day details
    public static class ItineraryDayDTO {
        private Integer dayNumber;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private String startLocation;
        private String endLocation;
        private Double totalDistanceKm;
        private Double estimatedTravelTimeHours;
        private Double dayBudget;
        private String notes;
        private List<ItineraryPlaceDTO> places;
        
        // Getters and Setters for ItineraryDayDTO
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
        
        public List<ItineraryPlaceDTO> getPlaces() { return places; }
        public void setPlaces(List<ItineraryPlaceDTO> places) { this.places = places; }
    }
    
    // Inner class for place details
    public static class ItineraryPlaceDTO {
        private String placeId;
        private String placeName;
        private String district;
        private String category;
        private String description;
        private Double latitude;
        private Double longitude;
        private Integer visitOrder;
        private LocalTime arrivalTime;
        private LocalTime departureTime;
        private Double estimatedVisitDurationHours;
        private Double totalEntryCost;
        private String transportFromPrevious;
        private Double transportCost;
        private Double distanceFromPreviousKm;
        private Double travelTimeFromPreviousHours;
        private String weatherCondition;
        private Double temperatureCelsius;
        private String notes;
        
        // Getters and Setters for ItineraryPlaceDTO
        public String getPlaceId() { return placeId; }
        public void setPlaceId(String placeId) { this.placeId = placeId; }
        
        public String getPlaceName() { return placeName; }
        public void setPlaceName(String placeName) { this.placeName = placeName; }
        
        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        
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
        
        public Double getTotalEntryCost() { return totalEntryCost; }
        public void setTotalEntryCost(Double totalEntryCost) { this.totalEntryCost = totalEntryCost; }
        
        public String getTransportFromPrevious() { return transportFromPrevious; }
        public void setTransportFromPrevious(String transportFromPrevious) { this.transportFromPrevious = transportFromPrevious; }
        
        public Double getTransportCost() { return transportCost; }
        public void setTransportCost(Double transportCost) { this.transportCost = transportCost; }
        
        public Double getDistanceFromPreviousKm() { return distanceFromPreviousKm; }
        public void setDistanceFromPreviousKm(Double distanceFromPreviousKm) { 
            this.distanceFromPreviousKm = distanceFromPreviousKm; 
        }
        
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
    
    // Constructors
    public ItineraryResponseDTO() {}
    
    // Getters and Setters for main class
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public Integer getTotalDays() { return totalDays; }
    public void setTotalDays(Integer totalDays) { this.totalDays = totalDays; }
    
    public Integer getTotalPeople() { return totalPeople; }
    public void setTotalPeople(Integer totalPeople) { this.totalPeople = totalPeople; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Double getTotalEstimatedCost() { return totalEstimatedCost; }
    public void setTotalEstimatedCost(Double totalEstimatedCost) { this.totalEstimatedCost = totalEstimatedCost; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public List<ItineraryDayDTO> getDays() { return days; }
    public void setDays(List<ItineraryDayDTO> days) { this.days = days; }
}

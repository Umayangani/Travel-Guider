package com.travelguider.backend.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class ItineraryRequestDTO {
    
    private String title;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate endDate;
    
    private Integer totalDays;
    private Integer adultsCount;
    private Integer childrenCount;
    private Integer studentsCount;
    private Integer foreignersCount;
    private List<String> preferredCategories;
    private String budgetRange;
    private String startingLocation;
    private String accommodationType;
    private String transportPreference;
    private String activityLevel;
    private Boolean includeWeather;
    private Integer maxTravelDistanceKm;
    private List<String> specificInterests;
    
    // Constructors
    public ItineraryRequestDTO() {}
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public Integer getTotalDays() { return totalDays; }
    public void setTotalDays(Integer totalDays) { this.totalDays = totalDays; }
    
    public Integer getAdultsCount() { return adultsCount; }
    public void setAdultsCount(Integer adultsCount) { this.adultsCount = adultsCount; }
    
    public Integer getChildrenCount() { return childrenCount; }
    public void setChildrenCount(Integer childrenCount) { this.childrenCount = childrenCount; }
    
    public Integer getStudentsCount() { return studentsCount; }
    public void setStudentsCount(Integer studentsCount) { this.studentsCount = studentsCount; }
    
    public Integer getForeignersCount() { return foreignersCount; }
    public void setForeignersCount(Integer foreignersCount) { this.foreignersCount = foreignersCount; }
    
    public List<String> getPreferredCategories() { return preferredCategories; }
    
    @JsonSetter("preferredCategories")
    public void setPreferredCategories(Object preferredCategories) {
        if (preferredCategories instanceof String) {
            // Handle comma-separated string
            String str = (String) preferredCategories;
            this.preferredCategories = str.isEmpty() ? new ArrayList<>() : Arrays.asList(str.split(",\\s*"));
        } else if (preferredCategories instanceof List) {
            // Handle list directly
            this.preferredCategories = (List<String>) preferredCategories;
        } else {
            this.preferredCategories = new ArrayList<>();
        }
    }
    
    public String getBudgetRange() { return budgetRange; }
    public void setBudgetRange(String budgetRange) { this.budgetRange = budgetRange; }
    
    public String getStartingLocation() { return startingLocation; }
    public void setStartingLocation(String startingLocation) { this.startingLocation = startingLocation; }
    
    public String getAccommodationType() { return accommodationType; }
    public void setAccommodationType(String accommodationType) { this.accommodationType = accommodationType; }
    
    public String getTransportPreference() { return transportPreference; }
    public void setTransportPreference(String transportPreference) { this.transportPreference = transportPreference; }
    
    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }
    
    public Boolean getIncludeWeather() { return includeWeather; }
    public void setIncludeWeather(Boolean includeWeather) { this.includeWeather = includeWeather; }
    
    public Integer getMaxTravelDistanceKm() { return maxTravelDistanceKm; }
    public void setMaxTravelDistanceKm(Integer maxTravelDistanceKm) { this.maxTravelDistanceKm = maxTravelDistanceKm; }
    
    public List<String> getSpecificInterests() { return specificInterests; }
    
    @JsonSetter("specificInterests")
    public void setSpecificInterests(Object specificInterests) {
        if (specificInterests instanceof String) {
            // Handle comma-separated string
            String str = (String) specificInterests;
            this.specificInterests = str.isEmpty() ? new ArrayList<>() : Arrays.asList(str.split(",\\s*"));
        } else if (specificInterests instanceof List) {
            // Handle list directly
            this.specificInterests = (List<String>) specificInterests;
        } else {
            this.specificInterests = new ArrayList<>();
        }
    }
    
    // Helper method to calculate total people
    public Integer getTotalPeople() {
        int total = 0;
        if (adultsCount != null) total += adultsCount;
        if (childrenCount != null) total += childrenCount;
        if (studentsCount != null) total += studentsCount;
        if (foreignersCount != null) total += foreignersCount;
        return total;
    }
}

package com.travelguider.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
public class UserPreferences {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", unique = true)
    private Long userId;
    
    @Column(name = "preferred_categories", length = 500)
    private String preferredCategories; // JSON array: ["beach", "forest", "cultural", "adventure"]
    
    @Column(name = "budget_range", length = 50)
    private String budgetRange; // "low", "medium", "high"
    
    @Column(name = "accommodation_type", length = 100)
    private String accommodationType; // "hotel", "guesthouse", "resort", "camping"
    
    @Column(name = "transport_preference", length = 100)
    private String transportPreference; // "bus", "train", "taxi", "own_vehicle"
    
    @Column(name = "activity_level", length = 50)
    private String activityLevel; // "relaxed", "moderate", "active"
    
    @Column(name = "food_preference", length = 100)
    private String foodPreference; // "local", "international", "vegetarian", "any"
    
    @Column(name = "group_type", length = 50)
    private String groupType; // "family", "friends", "couple", "solo"
    
    @Column(name = "accessibility_needs")
    private Boolean accessibilityNeeds;
    
    @Column(name = "preferred_start_time", length = 10)
    private String preferredStartTime; // "early", "morning", "afternoon"
    
    @Column(name = "max_travel_distance_km")
    private Integer maxTravelDistanceKm;
    
    @Column(name = "interests", length = 1000)
    private String interests; // JSON array of specific interests
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserPreferences() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.accessibilityNeeds = false;
        this.budgetRange = "medium";
        this.activityLevel = "moderate";
        this.maxTravelDistanceKm = 500;
    }
    
    public UserPreferences(Long userId) {
        this();
        this.userId = userId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getPreferredCategories() { return preferredCategories; }
    public void setPreferredCategories(String preferredCategories) { this.preferredCategories = preferredCategories; }
    
    public String getBudgetRange() { return budgetRange; }
    public void setBudgetRange(String budgetRange) { this.budgetRange = budgetRange; }
    
    public String getAccommodationType() { return accommodationType; }
    public void setAccommodationType(String accommodationType) { this.accommodationType = accommodationType; }
    
    public String getTransportPreference() { return transportPreference; }
    public void setTransportPreference(String transportPreference) { this.transportPreference = transportPreference; }
    
    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }
    
    public String getFoodPreference() { return foodPreference; }
    public void setFoodPreference(String foodPreference) { this.foodPreference = foodPreference; }
    
    public String getGroupType() { return groupType; }
    public void setGroupType(String groupType) { this.groupType = groupType; }
    
    public Boolean getAccessibilityNeeds() { return accessibilityNeeds; }
    public void setAccessibilityNeeds(Boolean accessibilityNeeds) { this.accessibilityNeeds = accessibilityNeeds; }
    
    public String getPreferredStartTime() { return preferredStartTime; }
    public void setPreferredStartTime(String preferredStartTime) { this.preferredStartTime = preferredStartTime; }
    
    public Integer getMaxTravelDistanceKm() { return maxTravelDistanceKm; }
    public void setMaxTravelDistanceKm(Integer maxTravelDistanceKm) { this.maxTravelDistanceKm = maxTravelDistanceKm; }
    
    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

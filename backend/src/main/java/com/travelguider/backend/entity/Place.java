package com.travelguider.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "places")
public class Place implements Serializable {
    @Id
    @Column(name = "place_id", length = 20)
    private String placeId;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "region", length = 50)
    private String region;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "estimated_time_to_visit")
    private Double estimatedTimeToVisit;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // Getters and setters
    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getEstimatedTimeToVisit() { return estimatedTimeToVisit; }
    public void setEstimatedTimeToVisit(Double estimatedTimeToVisit) { this.estimatedTimeToVisit = estimatedTimeToVisit; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}

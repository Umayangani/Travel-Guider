package com.travelguider.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "places")
@Getter
@Setter
public class Place {
    @Id
    @Column(name = "place_id")
    private String placeId;

    private String name;
    private String district;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String region;
    private String category;
    private Double estimatedTimeToVisit;
    private Double latitude;
    private Double longitude;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getEstimatedTimeToVisit() {
        return estimatedTimeToVisit;
    }

    public void setEstimatedTimeToVisit(Double estimatedTimeToVisit) {
        this.estimatedTimeToVisit = estimatedTimeToVisit;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}

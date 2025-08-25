package com.travelguider.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "place_entry_fees")
public class PlaceEntryFee implements Serializable {
    @Id
    @Column(name = "fee_id", length = 25)
    private String feeId;

    @Column(name = "place_id", length = 20)
    private String placeId;

    @Column(name = "foreign_adult")
    private Double foreignAdult;

    @Column(name = "foreign_child")
    private Double foreignChild;

    @Column(name = "local_adult")
    private Double localAdult;

    @Column(name = "local_child")
    private Double localChild;

    @Column(name = "student")
    private Double student;

    @Column(name = "free_entry")
    private Boolean freeEntry = false;

    // Getters and setters
    public String getFeeId() { return feeId; }
    public void setFeeId(String feeId) { this.feeId = feeId; }
    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }
    public Double getForeignAdult() { return foreignAdult; }
    public void setForeignAdult(Double foreignAdult) { this.foreignAdult = foreignAdult; }
    public Double getForeignChild() { return foreignChild; }
    public void setForeignChild(Double foreignChild) { this.foreignChild = foreignChild; }
    public Double getLocalAdult() { return localAdult; }
    public void setLocalAdult(Double localAdult) { this.localAdult = localAdult; }
    public Double getLocalChild() { return localChild; }
    public void setLocalChild(Double localChild) { this.localChild = localChild; }
    public Double getStudent() { return student; }
    public void setStudent(Double student) { this.student = student; }
    public Boolean getFreeEntry() { return freeEntry; }
    public void setFreeEntry(Boolean freeEntry) { this.freeEntry = freeEntry; }
}

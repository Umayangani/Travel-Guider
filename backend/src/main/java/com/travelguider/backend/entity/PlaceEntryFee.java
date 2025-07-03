package com.travelguider.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "place_entry_fees")
@Getter
@Setter
public class PlaceEntryFee {
    @Id
    @Column(name = "fee_id")
    private String feeId;

    @Column(name = "place_id")
    private String placeId;

    private Double foreignAdult;
    private Double foreignChild;
    private Double localAdult;
    private Double localChild;
    private Double student;
    private Boolean freeEntry;
}

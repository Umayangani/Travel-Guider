package com.travelguider.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "place_media")
@Getter
@Setter
public class PlaceMedia {
    @Id
    @Column(name = "media_id")
    private String mediaId;

    @Column(name = "place_id")
    private String placeId;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(columnDefinition = "TEXT")
    private String mediaUrl;

    public enum MediaType {
        image, video
    }
}

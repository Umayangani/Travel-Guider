package com.travelguider.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "place_media")
public class PlaceMedia implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", length = 20)
    private String placeId;

    @Column(name = "media_url", length = 255)
    private String mediaUrl;

    @Column(name = "media_type", length = 20)
    private String mediaType; // e.g. image, video

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
}

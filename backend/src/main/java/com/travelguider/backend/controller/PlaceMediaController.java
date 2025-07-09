package com.travelguider.backend.controller;

import com.travelguider.backend.entity.PlaceMedia;
import com.travelguider.backend.service.PlaceMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/place-media")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class PlaceMediaController {
    @Autowired
    private PlaceMediaService placeMediaService;

    @PostMapping("")
    public ResponseEntity<PlaceMedia> addMedia(@RequestBody PlaceMedia media) {
        PlaceMedia saved = placeMediaService.save(media);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{placeId}")
    public List<PlaceMedia> getMediaByPlace(@PathVariable String placeId) {
        return placeMediaService.getMediaByPlaceId(placeId);
    }
}

package com.travelguider.backend.service;

import com.travelguider.backend.entity.PlaceMedia;
import com.travelguider.backend.repository.PlaceMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlaceMediaService {
    @Autowired
    private PlaceMediaRepository placeMediaRepository;

    public PlaceMedia save(PlaceMedia media) {
        return placeMediaRepository.save(media);
    }

    public List<PlaceMedia> getMediaByPlaceId(String placeId) {
        return placeMediaRepository.findByPlaceId(placeId);
    }
}

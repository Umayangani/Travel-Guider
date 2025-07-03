package com.travelguider.backend.controller;

import com.travelguider.backend.entity.Place;
import com.travelguider.backend.entity.PlaceEntryFee;
import com.travelguider.backend.entity.PlaceMedia;
import com.travelguider.backend.entity.PlaceMedia.MediaType;
import com.travelguider.backend.repository.PlaceEntryFeeRepository;
import com.travelguider.backend.repository.PlaceMediaRepository;
import com.travelguider.backend.repository.PlaceRepository;
import com.travelguider.backend.service.LocalFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(origins = "http://localhost:3000")
public class PlaceController {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PlaceEntryFeeRepository feeRepository;

    @Autowired
    private PlaceMediaRepository mediaRepository;

    @Autowired
    private LocalFileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<String> addPlace(
            @RequestParam String name,
            @RequestParam String district,
            @RequestParam String description,
            @RequestParam String region,
            @RequestParam String category,
            @RequestParam(required = false) Double estimated_time_to_visit,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "false") Boolean free_entry,
            @RequestParam(required = false) Double foreign_adult,
            @RequestParam(required = false) Double foreign_child,
            @RequestParam(required = false) Double local_adult,
            @RequestParam(required = false) Double local_child,
            @RequestParam(required = false) Double student,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "video", required = false) MultipartFile video
    ) throws Exception {

        // Generate IDs
        String districtCode = district.substring(0, 3).toUpperCase();
        String placeId = "WE-" + districtCode + "-001";
        String feeId = placeId + "-FEE";

        // Save Place
        Place place = new Place();
        place.setPlaceId(placeId);
        place.setName(name);
        place.setDistrict(district);
        place.setDescription(description);
        place.setRegion(region);
        place.setCategory(category);
        place.setEstimatedTimeToVisit(estimated_time_to_visit);
        place.setLatitude(latitude);
        place.setLongitude(longitude);
        placeRepository.save(place);

        // Save Fee
        PlaceEntryFee fee = new PlaceEntryFee();
        fee.setFeeId(feeId);
        fee.setPlaceId(placeId);
        fee.setForeignAdult(foreign_adult);
        fee.setForeignChild(foreign_child);
        fee.setLocalAdult(local_adult);
        fee.setLocalChild(local_child);
        fee.setStudent(student);
        fee.setFreeEntry(free_entry);
        feeRepository.save(fee);

        // Save Media
        int imgCount = 1;
        if (images != null) {
            for (MultipartFile img : images) {
                String mediaId = placeId + "-IMG-" + imgCount++;
                String url = fileStorageService.saveFile(img);

                PlaceMedia media = new PlaceMedia();
                media.setMediaId(mediaId);
                media.setPlaceId(placeId);
                media.setMediaType(MediaType.image);
                media.setMediaUrl(url);
                mediaRepository.save(media);
            }
        }
        if (video != null) {
            String mediaId = placeId + "-VID";
            String url = fileStorageService.saveFile(video);

            PlaceMedia media = new PlaceMedia();
            media.setMediaId(mediaId);
            media.setPlaceId(placeId);
            media.setMediaType(MediaType.video);
            media.setMediaUrl(url);
            mediaRepository.save(media);
        }

        return ResponseEntity.ok("Place added successfully with ID: " + placeId);
    }
}

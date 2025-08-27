package com.travelguider.backend.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlaceTest {

    @Test
    public void testPlaceEntity() {
        Place place = new Place();
        place.setName("Test Place");
        place.setDistrict("Test District");
        place.setDescription("Test Description");
        place.setRegion("Central");
        place.setCategory("Historical");
        place.setEstimatedTimeToVisit(2.5);
        place.setLatitude(7.2964);
        place.setLongitude(80.6350);
        
        assertEquals("Test Place", place.getName());
        assertEquals("Test District", place.getDistrict());
        assertEquals("Test Description", place.getDescription());
        assertEquals("Central", place.getRegion());
        assertEquals("Historical", place.getCategory());
        assertEquals(2.5, place.getEstimatedTimeToVisit());
        assertEquals(7.2964, place.getLatitude());
        assertEquals(80.6350, place.getLongitude());
    }
}

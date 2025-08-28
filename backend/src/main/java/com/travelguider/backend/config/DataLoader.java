package com.travelguider.backend.config;

import com.travelguider.backend.entity.Place;
import com.travelguider.backend.entity.User;
import com.travelguider.backend.entity.Role;
import com.travelguider.backend.repository.PlaceRepository;
import com.travelguider.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private PlaceRepository placeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Load default user
        if (userRepository.count() == 0) {
            System.out.println("👤 Creating default user...");
            createDefaultUser();
        }
        
        // Only load sample data if database is empty
        if (placeRepository.count() == 0) {
            System.out.println("🌱 Loading sample places data...");
            loadSamplePlaces();
            System.out.println("✅ Sample places data loaded successfully!");
        } else {
            System.out.println("📊 Database already contains " + placeRepository.count() + " places");
        }
    }
    
    private void createDefaultUser() {
        User defaultUser = new User();
        defaultUser.setUserId(1L);
        defaultUser.setName("Test User");
        defaultUser.setEmail("test@example.com");
        defaultUser.setPassword(passwordEncoder.encode("password"));
        defaultUser.setRole(Role.USER);
        defaultUser.setDob(LocalDate.of(1990, 1, 1));
        defaultUser.setAddress("Colombo, Sri Lanka");
        userRepository.save(defaultUser);
        System.out.println("✅ Default user created (email: test@example.com, password: password)");
    }

    private void loadSamplePlaces() {
        List<Place> places = Arrays.asList(
            createPlace("COL001", "Galle Face Green", "Colombo", 
                       "Galle Face is a big open area by the sea in Colombo where people go to relax and have fun. Many families and friends come here to eat snacks, fly kites, and watch the sunset", 
                       "West", "Beach", 2.0, 6.9271, 79.8612),
            
            createPlace("COL002", "Viharamahadevi Park", "Colombo", 
                       "Relaxed park with jogging trails, kids play areas, space for picnics & activities like cycling.", 
                       "West", "Nature", 2.5, 6.9147, 79.8614),
            
            createPlace("COL003", "Lotus Tower", "Colombo", 
                       "Lotus Tower, Colombo, Sri Lanka is a sleek, lotus‑shaped communications and observation tower rising 350 m above the cityscape and Beira Lake. It offers panoramic views, a revolving restaurant, observation deck, and leisure facilities for visitors", 
                       "West", "Scenic", 3.0, 6.9164, 79.8491),
            
            createPlace("KAN001", "Temple of the Tooth", "Kandy", 
                       "The Sri Dalada Maligawa or the Temple of the Sacred Tooth Relic is a Buddhist temple in Kandy, Sri Lanka. It is located in the royal palace complex of the former Kingdom of Kandy", 
                       "Central", "Temple", 3.0, 7.2936, 80.6414),
            
            createPlace("KAN002", "Kandy Lake", "Kandy", 
                       "Kandy Lake, also known as Kiri Muhuda or the Sea of Milk, is an artificial lake in the heart of the hill city of Kandy, Sri Lanka, built in 1807 by King Sri Vikrama Rajasinha next to the Temple of the Tooth", 
                       "Central", "Nature", 2.0, 7.2933, 80.6424),
            
            createPlace("GAL001", "Galle Fort", "Galle", 
                       "Galle Fort is a historical, archaeological and architectural heritage monument, which even after more than 432 years maintains a polished appearance, due to extensive reconstruction work done by Archaeological Department of Sri Lanka", 
                       "South", "Culture", 4.0, 6.0329, 80.2168),
            
            createPlace("GAL002", "Unawatuna Beach", "Galle", 
                       "Unawatuna is a coastal town in Galle district of Sri Lanka. Unawatuna is a major tourist attraction in Sri Lanka and famous for its beautiful beach and corals", 
                       "South", "Beach", 3.0, 6.0108, 80.2506),
            
            createPlace("NUW001", "Nuwara Eliya", "Nuwara Eliya", 
                       "Nuwara Eliya is a resort town in the tea country hills of central Sri Lanka. The naturally landscaped Hakgala Botanical Gardens displays roses and tree ferns, and shelters monkeys and blue magpies", 
                       "Central", "Nature", 6.0, 6.9497, 80.7891),
            
            createPlace("SIG001", "Sigiriya Rock", "Matale", 
                       "Sigiriya or Sinhagiri is an ancient rock fortress located in the northern Matale District near the town of Dambulla in the Central Province, Sri Lanka", 
                       "Central", "Adventure", 4.0, 7.9569, 80.7603),
            
            createPlace("DAM001", "Dambulla Cave Temple", "Matale", 
                       "Dambulla cave temple also known as the Golden Temple of Dambulla is a World Heritage Site in Sri Lanka, situated in the central part of the country", 
                       "Central", "Temple", 3.0, 7.8567, 80.6489),

            createPlace("HIK001", "World's End", "Nuwara Eliya", 
                       "World's End is a sheer precipice with a drop of about 870 m (2,854 ft), located in Horton Plains National Park", 
                       "Central", "Adventure", 5.0, 6.8068, 80.8053),

            createPlace("WIL001", "Yala National Park", "Hambantota", 
                       "Yala National Park is the most visited and second largest national park in Sri Lanka, famous for its variety of wild animals, particularly leopards", 
                       "South", "Wildlife", 6.0, 6.3725, 81.5185),

            createPlace("BEA001", "Mirissa Beach", "Matara", 
                       "Mirissa is a small town on the south coast of Sri Lanka, famous for whale watching, beautiful beaches and coconut palm trees", 
                       "South", "Beach", 4.0, 5.9487, 80.4588),

            createPlace("CUL001", "Polonnaruwa", "Polonnaruwa", 
                       "Polonnaruwa is the main town of Polonnaruwa District in North Central Province, Sri Lanka. The second most ancient of Sri Lanka's kingdoms", 
                       "North Central", "Culture", 5.0, 7.9403, 81.0188),

            createPlace("SPI001", "Adam's Peak", "Ratnapura", 
                       "Adam's Peak is a 2,243 m tall conical mountain located in central Sri Lanka. It is well known for the Sri Pada, a 1.8 m rock formation near the summit", 
                       "Sabaragamuwa", "Adventure", 8.0, 6.8094, 80.4991)
        );

        placeRepository.saveAll(places);
    }

    private Place createPlace(String placeId, String name, String district, String description, 
                             String region, String category, Double estimatedHours, 
                             Double latitude, Double longitude) {
        Place place = new Place();
        place.setPlaceId(placeId);
        place.setName(name);
        place.setDistrict(district);
        place.setDescription(description);
        place.setRegion(region);
        place.setCategory(category);
        place.setEstimatedTimeToVisit(estimatedHours);
        place.setLatitude(latitude);
        place.setLongitude(longitude);
        return place;
    }
}

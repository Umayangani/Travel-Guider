package com.travelguider.backend.service;

import com.travelguider.backend.dto.ItineraryRequestDTO;
import com.travelguider.backend.dto.ItineraryResponseDTO;
import com.travelguider.backend.entity.Place;
import com.travelguider.backend.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItineraryService {
    
    @Autowired
    private PlaceRepository placeRepository;
    
    @Autowired
    private MLItineraryService mlItineraryService;
    
    /**
     * Generate itinerary - Uses ML if available, falls back to rule-based approach
     */
    public ItineraryResponseDTO generateItinerary(Long userId, ItineraryRequestDTO request) {
        try {
            System.out.println("🎯 Generating itinerary with hybrid ML + Rule-based approach");
            
            // First try ML-powered generation
            if (mlItineraryService.isMLServiceAvailable()) {
                try {
                    System.out.println("🤖 Using ML-powered itinerary generation");
                    return mlItineraryService.generateMLItinerary(userId, request);
                } catch (Exception e) {
                    System.err.println("⚠️ ML generation failed, falling back to rule-based: " + e.getMessage());
                }
            } else {
                System.out.println("📋 ML service not available, using rule-based approach");
            }
            
            // Fallback to rule-based approach
            return generateRuleBasedItinerary(userId, request);
            
        } catch (Exception e) {
            System.err.println("❌ Error in itinerary generation: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate itinerary: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate practical round-trip itinerary starting and ending in Colombo (Rule-based)
     */
    private ItineraryResponseDTO generateRuleBasedItinerary(Long userId, ItineraryRequestDTO request) {
        try {
            System.out.println("🚀 Generating rule-based round-trip itinerary from Colombo");
            
            // Get all places and filter by preferences
            List<Place> allPlaces = placeRepository.findAll();
            System.out.println("🏛️ Total places in database: " + allPlaces.size());
            
            // Filter places based on preferences and ensure they have valid coordinates
            List<Place> filteredPlaces = allPlaces.stream()
                .filter(place -> matchesCategories(place, request.getPreferredCategories()))
                .filter(place -> place.getLatitude() != null && place.getLongitude() != null)
                .collect(Collectors.toList());
            
            System.out.println("🎯 Filtered places matching preferences: " + filteredPlaces.size());
            System.out.println("📝 Preferences: " + request.getPreferredCategories());
            
            // Colombo coordinates (always start and end point)
            double colomboLat = 6.9271;
            double colomboLon = 79.8612;
            
            // Create regional clusters for practical routing
            Map<String, List<Place>> regionalClusters = createRegionalClusters(filteredPlaces, colomboLat, colomboLon);
            
            // Generate round-trip itinerary
            List<List<Place>> dailyPlaceClusters = generateRoundTripItinerary(regionalClusters, request.getTotalDays(), colomboLat, colomboLon);
            
            // Create response
            ItineraryResponseDTO response = new ItineraryResponseDTO();
            response.setId(System.currentTimeMillis());
            response.setTitle("Smart Round-Trip Travel Itinerary from Colombo");
            response.setStartDate(request.getStartDate());
            response.setEndDate(request.getEndDate());
            response.setTotalDays(request.getTotalDays());
            response.setTotalPeople(request.getAdultsCount() + request.getChildrenCount());
            response.setStatus("Generated - Smart Rule-Based System");
            response.setCreatedAt(java.time.LocalDateTime.now());
            
            // Create optimized daily itineraries
            List<ItineraryResponseDTO.ItineraryDayDTO> days = new ArrayList<>();
            double totalCost = 0.0;
            
            for (int day = 1; day <= request.getTotalDays() && day <= dailyPlaceClusters.size(); day++) {
                List<Place> dayPlaces = dailyPlaceClusters.get(day - 1);
                
                ItineraryResponseDTO.ItineraryDayDTO dayDTO = createDayItinerary(
                    day, request, dayPlaces, colomboLat, colomboLon, request.getTotalDays()
                );
                
                totalCost += dayDTO.getDayBudget();
                days.add(dayDTO);
            }
            
            response.setDays(days);
            response.setTotalEstimatedCost(totalCost);
            
            return response;
            
        } catch (Exception e) {
            System.err.println("❌ Error generating rule-based itinerary: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate rule-based itinerary: " + e.getMessage(), e);
        }
    }
    
    /**
     * Create regional clusters for efficient routing
     */
    private Map<String, List<Place>> createRegionalClusters(List<Place> places, double colomboLat, double colomboLon) {
        Map<String, List<Place>> clusters = new HashMap<>();
        
        // Initialize regions
        clusters.put("Colombo", new ArrayList<>());
        clusters.put("Western", new ArrayList<>());
        clusters.put("Central", new ArrayList<>());
        clusters.put("Southern", new ArrayList<>());
        clusters.put("HillCountry", new ArrayList<>());
        clusters.put("Ancient", new ArrayList<>());
        
        for (Place place : places) {
            double distance = calculateDistance(colomboLat, colomboLon, place.getLatitude(), place.getLongitude());
            String region = categorizeByRegion(place, distance);
            
            clusters.get(region).add(place);
            System.out.println("📍 " + place.getName() + " (" + place.getDistrict() + ") -> " + region + " region, " + String.format("%.1f", distance) + "km from Colombo");
        }
        
        return clusters;
    }
    
    /**
     * Categorize places by geographical regions
     */
    private String categorizeByRegion(Place place, double distanceFromColombo) {
        String district = place.getDistrict().toLowerCase();
        
        // Specific regional assignments
        if (district.contains("colombo") || (district.contains("gampaha") && distanceFromColombo <= 30)) {
            return "Colombo";
        } else if (district.contains("kalutara") || district.contains("gampaha")) {
            return "Western";
        } else if (district.contains("kandy") || district.contains("matale")) {
            return "Central";
        } else if (district.contains("galle") || district.contains("matara") || district.contains("hambantota")) {
            return "Southern";
        } else if (district.contains("nuwara eliya") || district.contains("badulla")) {
            return "HillCountry";
        } else if (district.contains("polonnaruwa") || district.contains("anuradhapura")) {
            return "Ancient";
        } else {
            // Fallback based on distance
            if (distanceFromColombo <= 30) return "Colombo";
            else if (distanceFromColombo <= 60) return "Western";
            else if (distanceFromColombo <= 120) return "Central";
            else return "Ancient";
        }
    }
    
    /**
     * Generate round-trip itinerary based on regional clusters
     */
    private List<List<Place>> generateRoundTripItinerary(Map<String, List<Place>> clusters, int totalDays, double colomboLat, double colomboLon) {
        List<List<Place>> dailyItinerary = new ArrayList<>();
        
        if (totalDays == 1) {
            // 1-day trip: Stay around Colombo region (max 10 hours)
            List<Place> oneDayPlaces = createOneDayItinerary(clusters);
            dailyItinerary.add(oneDayPlaces);
            
        } else if (totalDays == 2) {
            // 2-day trip: Colombo + one nearby region
            dailyItinerary.add(createColomboDayItinerary(clusters, true));  // Day 1: Start from Colombo
            dailyItinerary.add(createRegionalDayItinerary(clusters, "Western", true)); // Day 2: Western + return
            
        } else if (totalDays == 3) {
            // 3-day trip: Colombo + Central region (Kandy)
            dailyItinerary.add(createColomboDayItinerary(clusters, false)); // Day 1: Colombo area
            dailyItinerary.add(createRegionalDayItinerary(clusters, "Central", false)); // Day 2: Kandy area
            dailyItinerary.add(createReturnDayItinerary(clusters, "Central")); // Day 3: Travel back to Colombo
            
        } else if (totalDays >= 4) {
            // 4+ day trip: Multi-region tour
            dailyItinerary.add(createColomboDayItinerary(clusters, false)); // Day 1: Start in Colombo
            
            // Middle days: Visit different regions
            String[] regionOrder = {"Central", "HillCountry", "Southern", "Ancient"};
            for (int i = 1; i < totalDays - 1 && i < regionOrder.length + 1; i++) {
                if (i <= regionOrder.length) {
                    dailyItinerary.add(createRegionalDayItinerary(clusters, regionOrder[i-1], false));
                }
            }
            
            // Last day: Return to Colombo
            dailyItinerary.add(createReturnDayItinerary(clusters, regionOrder[Math.min(totalDays-3, regionOrder.length-1)]));
        }
        
        return dailyItinerary;
    }
    
    /**
     * Create one-day itinerary around Colombo (max 10 hours)
     */
    private List<Place> createOneDayItinerary(Map<String, List<Place>> clusters) {
        List<Place> dayPlaces = new ArrayList<>();
        
        // Add 3-4 places from Colombo region
        List<Place> colomboPlaces = clusters.getOrDefault("Colombo", new ArrayList<>());
        List<Place> westernPlaces = clusters.getOrDefault("Western", new ArrayList<>());
        
        int maxPlaces = 4;
        int added = 0;
        
        // Prioritize Colombo places
        for (Place place : colomboPlaces) {
            if (added < maxPlaces) {
                dayPlaces.add(place);
                added++;
            }
        }
        
        // Add nearby Western places if needed
        for (Place place : westernPlaces) {
            if (added < maxPlaces) {
                dayPlaces.add(place);
                added++;
            }
        }
        
        System.out.println("📅 One-day itinerary: " + added + " places around Colombo");
        return dayPlaces;
    }
    
    /**
     * Create Colombo-based day itinerary
     */
    private List<Place> createColomboDayItinerary(Map<String, List<Place>> clusters, boolean isReturnDay) {
        List<Place> dayPlaces = new ArrayList<>();
        List<Place> colomboPlaces = clusters.getOrDefault("Colombo", new ArrayList<>());
        
        int maxPlaces = isReturnDay ? 4 : 3; // More places if it's a return day
        for (int i = 0; i < Math.min(maxPlaces, colomboPlaces.size()); i++) {
            dayPlaces.add(colomboPlaces.get(i));
        }
        
        System.out.println("📅 Colombo day: " + dayPlaces.size() + " places in Colombo region");
        return dayPlaces;
    }
    
    /**
     * Create regional day itinerary
     */
    private List<Place> createRegionalDayItinerary(Map<String, List<Place>> clusters, String region, boolean isReturnDay) {
        List<Place> dayPlaces = new ArrayList<>();
        List<Place> regionPlaces = clusters.getOrDefault(region, new ArrayList<>());
        
        int maxPlaces = isReturnDay ? 3 : 4; // Fewer places if returning same day
        for (int i = 0; i < Math.min(maxPlaces, regionPlaces.size()); i++) {
            dayPlaces.add(regionPlaces.get(i));
        }
        
        System.out.println("📅 " + region + " day: " + dayPlaces.size() + " places in " + region + " region");
        return dayPlaces;
    }
    
    /**
     * Create return day itinerary (travel back to Colombo)
     */
    private List<Place> createReturnDayItinerary(Map<String, List<Place>> clusters, String fromRegion) {
        List<Place> dayPlaces = new ArrayList<>();
        
        // Add 1-2 places on the way back to Colombo
        List<Place> westernPlaces = clusters.getOrDefault("Western", new ArrayList<>());
        List<Place> colomboPlaces = clusters.getOrDefault("Colombo", new ArrayList<>());
        
        // Add 1 Western place (on the way)
        if (!westernPlaces.isEmpty()) {
            dayPlaces.add(westernPlaces.get(0));
        }
        
        // Add 1-2 Colombo places (final destinations)
        for (int i = 0; i < Math.min(2, colomboPlaces.size()); i++) {
            if (!dayPlaces.contains(colomboPlaces.get(i))) {
                dayPlaces.add(colomboPlaces.get(i));
            }
        }
        
        System.out.println("📅 Return day: " + dayPlaces.size() + " places returning to Colombo from " + fromRegion);
        return dayPlaces;
    }
    
    /**
     * Create detailed day itinerary with proper Colombo start/end
     */
    private ItineraryResponseDTO.ItineraryDayDTO createDayItinerary(int dayNumber, ItineraryRequestDTO request, List<Place> dayPlaces, double colomboLat, double colomboLon, int totalDays) {
        ItineraryResponseDTO.ItineraryDayDTO dayDTO = new ItineraryResponseDTO.ItineraryDayDTO();
        dayDTO.setDayNumber(dayNumber);
        dayDTO.setDate(request.getStartDate().plusDays(dayNumber - 1));
        dayDTO.setStartTime(java.time.LocalTime.of(8, 0));
        dayDTO.setEndTime(java.time.LocalTime.of(18, 0));
        
        // All trips start and end in Colombo
        dayDTO.setStartLocation(dayNumber == 1 ? "Colombo (Starting Point)" : "Colombo");
        dayDTO.setEndLocation(dayNumber == totalDays ? "Colombo (Return)" : "Regional Area");
        
        List<ItineraryResponseDTO.ItineraryPlaceDTO> placeDTOs = new ArrayList<>();
        double dayTotalDistance = 0.0;
        double dayTotalTime = 0.0;
        double dayBudget = 0.0;
        
        // Always start from Colombo
        double currentLat = colomboLat;
        double currentLon = colomboLon;
        
        for (int i = 0; i < dayPlaces.size(); i++) {
            Place place = dayPlaces.get(i);
            
            ItineraryResponseDTO.ItineraryPlaceDTO placeDTO = new ItineraryResponseDTO.ItineraryPlaceDTO();
            placeDTO.setPlaceId(place.getPlaceId());
            placeDTO.setPlaceName(place.getName());
            placeDTO.setCategory(place.getCategory());
            placeDTO.setDistrict(place.getDistrict());
            placeDTO.setDescription(place.getDescription());
            placeDTO.setLatitude(place.getLatitude());
            placeDTO.setLongitude(place.getLongitude());
            placeDTO.setVisitOrder(i + 1);
            
            // Calculate distance from previous location
            double distanceKm = calculateDistance(currentLat, currentLon, place.getLatitude(), place.getLongitude());
            double travelTimeHours = calculateTravelTime(distanceKm, request.getTransportPreference());
            
            placeDTO.setDistanceFromPreviousKm(distanceKm);
            placeDTO.setTravelTimeFromPreviousHours(travelTimeHours);
            placeDTO.setTransportFromPrevious(request.getTransportPreference() != null ? request.getTransportPreference() : "Car");
            placeDTO.setTransportCost(calculateTransportCost(distanceKm, request.getTransportPreference()));
            
            // Visit duration
            double visitDuration = place.getEstimatedTimeToVisit() != null ? place.getEstimatedTimeToVisit() : 2.0;
            placeDTO.setEstimatedVisitDurationHours(visitDuration);
            
            // Calculate arrival and departure times
            int startHour = 8 + (int)(dayTotalTime + travelTimeHours);
            int startMinute = (int)((dayTotalTime + travelTimeHours - (int)(dayTotalTime + travelTimeHours)) * 60);
            placeDTO.setArrivalTime(java.time.LocalTime.of(Math.min(startHour, 17), startMinute));
            
            int endHour = startHour + (int)visitDuration;
            int endMinute = startMinute + (int)((visitDuration - (int)visitDuration) * 60);
            placeDTO.setDepartureTime(java.time.LocalTime.of(Math.min(endHour, 18), endMinute % 60));
            
            // Entry costs
            double entryCost = 500.0;
            placeDTO.setTotalEntryCost(entryCost);
            
            // Weather
            placeDTO.setWeatherCondition("Partly Cloudy");
            placeDTO.setTemperatureCelsius(27.0 + Math.random() * 6);
            
            dayTotalDistance += distanceKm;
            dayTotalTime += travelTimeHours + visitDuration;
            dayBudget += entryCost + placeDTO.getTransportCost();
            
            placeDTOs.add(placeDTO);
            
            // Update current position
            currentLat = place.getLatitude();
            currentLon = place.getLongitude();
        }
        
        // Add return trip to Colombo at the end of each day
        if (!dayPlaces.isEmpty()) {
            double returnDistance = calculateDistance(currentLat, currentLon, colomboLat, colomboLon);
            double returnTime = calculateTravelTime(returnDistance, request.getTransportPreference());
            double returnCost = calculateTransportCost(returnDistance, request.getTransportPreference());
            
            dayTotalDistance += returnDistance;
            dayTotalTime += returnTime;
            dayBudget += returnCost;
            
            System.out.println("🔄 Day " + dayNumber + " return to Colombo: " + String.format("%.1f", returnDistance) + "km, " + String.format("%.1f", returnTime) + "h");
        }
        
        dayDTO.setPlaces(placeDTOs);
        dayDTO.setTotalDistanceKm(dayTotalDistance);
        dayDTO.setEstimatedTravelTimeHours(dayTotalTime);
        dayDTO.setDayBudget(dayBudget);
        
        return dayDTO;
    }
    
    /**
     * Calculate distance between two points using Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Distance in km
        
        return Math.round(distance * 100.0) / 100.0; // Round to 2 decimal places
    }
    
    /**
     * Calculate travel time based on distance and transport mode
     */
    private double calculateTravelTime(double distanceKm, String transportMode) {
        double speedKmH;
        
        switch (transportMode != null ? transportMode.toLowerCase() : "car") {
            case "bus":
                speedKmH = 40; // Average bus speed including stops
                break;
            case "train":
                speedKmH = 50; // Average train speed
                break;
            case "car":
            case "private":
            default:
                speedKmH = 60; // Average car speed on Sri Lankan roads
                break;
        }
        
        return Math.round((distanceKm / speedKmH) * 100.0) / 100.0; // Round to 2 decimal places
    }
    
    /**
     * Calculate transport cost based on distance and mode
     */
    private double calculateTransportCost(double distanceKm, String transportMode) {
        double costPerKm;
        
        switch (transportMode != null ? transportMode.toLowerCase() : "car") {
            case "bus":
                costPerKm = 5; // LKR per km for bus
                break;
            case "train":
                costPerKm = 3; // LKR per km for train
                break;
            case "car":
            case "private":
            default:
                costPerKm = 15; // LKR per km for private car (fuel + wear)
                break;
        }
        
        return Math.round(distanceKm * costPerKm);
    }
    
    private boolean matchesCategories(Place place, List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return true;
        }
        
        return categories.stream()
            .anyMatch(category -> place.getCategory() != null && 
                     place.getCategory().toLowerCase().contains(category.toLowerCase()));
    }
    
    /**
     * Stub methods for controller compatibility
     */
    public List<ItineraryResponseDTO> getUserItineraries(Long userId) {
        return new ArrayList<>();
    }
    
    public ItineraryResponseDTO getItineraryById(Long itineraryId, Long userId) {
        throw new RuntimeException("Itinerary not found");
    }
    
    public void deleteItinerary(Long itineraryId, Long userId) {
        // Stub implementation
    }
    
    public List<Map<String, Object>> generateRecommendations(Map<String, Object> preferences) {
        // Get user preferences
        String preferredRegion = (String) preferences.get("region");
        String preferredCategory = (String) preferences.get("category");
        Integer duration = (Integer) preferences.getOrDefault("duration", 3);
        
        // Fetch all places
        List<Place> allPlaces = placeRepository.findAll();
        
        // Filter places based on preferences
        List<Place> filteredPlaces = allPlaces.stream()
            .filter(place -> preferredRegion == null || place.getRegion().equalsIgnoreCase(preferredRegion))
            .filter(place -> preferredCategory == null || place.getCategory().equalsIgnoreCase(preferredCategory))
            .collect(Collectors.toList());
        
        // Generate recommendations
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        for (Place place : filteredPlaces.stream().limit(duration * 2).collect(Collectors.toList())) {
            Map<String, Object> recommendation = new HashMap<>();
            recommendation.put("placeId", place.getPlaceId());
            recommendation.put("placeName", place.getName());
            recommendation.put("region", place.getRegion());
            recommendation.put("district", place.getDistrict());
            recommendation.put("category", place.getCategory());
            recommendation.put("description", place.getDescription());
            recommendation.put("coordinates", Map.of(
                "latitude", place.getLatitude(),
                "longitude", place.getLongitude()
            ));
            
            // Calculate recommendation score based on simple criteria
            double score = calculateRecommendationScore(place, preferences);
            recommendation.put("recommendationScore", score);
            
            recommendations.add(recommendation);
        }
        
        // Sort by recommendation score
        recommendations.sort((a, b) -> Double.compare(
            (Double) b.get("recommendationScore"),
            (Double) a.get("recommendationScore")
        ));
        
        return recommendations.stream().limit(duration).collect(Collectors.toList());
    }
    
    private double calculateRecommendationScore(Place place, Map<String, Object> preferences) {
        double score = 0.5; // Base score
        
        String preferredRegion = (String) preferences.get("region");
        String preferredCategory = (String) preferences.get("category");
        
        // Region match bonus
        if (preferredRegion != null && place.getRegion().equalsIgnoreCase(preferredRegion)) {
            score += 0.3;
        }
        
        // Category match bonus
        if (preferredCategory != null && place.getCategory().equalsIgnoreCase(preferredCategory)) {
            score += 0.2;
        }
        
        // Add some randomness for variety
        score += Math.random() * 0.1;
        
        return Math.min(1.0, score);
    }
    
    public List<Map<String, Object>> getAvailableDestinations() {
        List<Place> places = placeRepository.findAll();
        
        return places.stream()
            .map(place -> {
                Map<String, Object> destination = new HashMap<>();
                destination.put("placeId", place.getPlaceId());
                destination.put("placeName", place.getName());
                destination.put("region", place.getRegion());
                destination.put("district", place.getDistrict());
                destination.put("category", place.getCategory());
                return destination;
            })
            .collect(Collectors.toList());
    }
}

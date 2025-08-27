package com.travelguider.backend.util;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlaceIdGenerator {
    private final Map<String, AtomicInteger> districtCounters = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> feeCounters = new ConcurrentHashMap<>();

    public String generatePlaceId(String district) {
        String prefix = district.replaceAll("[^a-zA-Z0-9]", "")
                              .substring(0, Math.min(district.length(), 3))
                              .toLowerCase();
        AtomicInteger counter = districtCounters.computeIfAbsent(prefix, k -> new AtomicInteger(0));
        int number = counter.incrementAndGet();
        return String.format("%s%03d", prefix, number);
    }

    public String generateEntryFeeId(String placeId) {
        String prefix = placeId + "fee";
        AtomicInteger counter = feeCounters.computeIfAbsent(prefix, k -> new AtomicInteger(0));
        int number = counter.incrementAndGet();
        return String.format("%s%03d", prefix, number);
    }
}

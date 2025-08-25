package com.travelguider.backend.service;

import com.travelguider.backend.entity.BusSchedule;
import com.travelguider.backend.repository.BusScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BusScheduleService {
    @Autowired
    private BusScheduleRepository busScheduleRepository;

    public BusSchedule addSchedule(BusSchedule schedule) {
        // Generate custom ID: {from}-{to}-{NNN}
        if (schedule.getId() == null || schedule.getId().isEmpty()) {
            String from = schedule.getDepartureLocation().replaceAll("\\s+", "").toUpperCase();
            String to = schedule.getArrivalLocation().replaceAll("\\s+", "").toUpperCase();
            String fromShort = from.length() >= 3 ? from.substring(0, 3) : from;
            String toShort = to.length() >= 3 ? to.substring(0, 3) : to;
            long count = busScheduleRepository.countByFromAndTo(schedule.getDepartureLocation(), schedule.getArrivalLocation()) + 1;
            String sequence = String.format("%03d", count);
            String customId = fromShort + "-" + toShort + "-" + sequence;
            schedule.setId(customId);
        }
        return busScheduleRepository.save(schedule);
    }

    public List<BusSchedule> getAllSchedules() {
        return busScheduleRepository.findAll();
    }

    public Optional<BusSchedule> getScheduleById(String id) {
        return busScheduleRepository.findById(id);
    }

    public void deleteSchedule(String id) {
        busScheduleRepository.deleteById(id);
    }
}

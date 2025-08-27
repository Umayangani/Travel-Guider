package com.travelguider.backend.service;

import com.travelguider.backend.entity.TrainSchedule;
import com.travelguider.backend.repository.TrainScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainScheduleService {
    @Autowired
    private TrainScheduleRepository trainScheduleRepository;

    public TrainSchedule addSchedule(TrainSchedule schedule) {
        // Only generate ID if it's a new schedule (no ID set)
        if (schedule.getScheduleId() == null || schedule.getScheduleId().isEmpty()) {
            String from = schedule.getFromStation().replaceAll("\\s+", "").toUpperCase();
            String to = schedule.getToStation().replaceAll("\\s+", "").toUpperCase();
            String fromShort = from.length() >= 3 ? from.substring(0, 3) : from;
            String toShort = to.length() >= 3 ? to.substring(0, 3) : to;
            long count = trainScheduleRepository.countByFromStationAndToStation(schedule.getFromStation(), schedule.getToStation()) + 1;
            String sequence = String.format("%03d", count);
            String customId = fromShort + "-" + toShort + "-" + sequence;
            schedule.setScheduleId(customId);
        }
        return trainScheduleRepository.save(schedule);
    }


    public TrainSchedule updateSchedule(TrainSchedule schedule) {
        // Do not change scheduleId, just save the updated entity
        return trainScheduleRepository.save(schedule);
    }

    public List<TrainSchedule> getAllSchedules() {
        return trainScheduleRepository.findAll();
    }

    public Optional<TrainSchedule> getScheduleById(String id) {
        return trainScheduleRepository.findById(id);
    }

    public void deleteSchedule(String id) {
        trainScheduleRepository.deleteById(id);
    }
    
    public List<TrainSchedule> searchSchedules(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSchedules();
        }
        return trainScheduleRepository.searchSchedules(keyword.trim());
    }
    
    public List<TrainSchedule> findByStations(String fromStation, String toStation) {
        if ((fromStation == null || fromStation.trim().isEmpty()) && 
            (toStation == null || toStation.trim().isEmpty())) {
            return getAllSchedules();
        }
        return trainScheduleRepository.findByStations(
            fromStation != null ? fromStation.trim() : "",
            toStation != null ? toStation.trim() : ""
        );
    }
}

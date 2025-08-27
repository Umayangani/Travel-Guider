package com.travelguider.backend.controller;

import com.travelguider.backend.entity.TrainSchedule;
import com.travelguider.backend.service.TrainScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/train-schedules")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class TrainScheduleController {
    @Autowired
    private TrainScheduleService trainScheduleService;

    @PostMapping("")
    public ResponseEntity<TrainSchedule> addSchedule(@RequestBody TrainSchedule schedule) {
        TrainSchedule saved = trainScheduleService.addSchedule(schedule);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("")
    public List<TrainSchedule> getAllSchedules() {
        return trainScheduleService.getAllSchedules();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainSchedule> getScheduleById(@PathVariable String id) {
        Optional<TrainSchedule> schedule = trainScheduleService.getScheduleById(id);
        return schedule.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<TrainSchedule> updateSchedule(@PathVariable String id, @RequestBody TrainSchedule updated) {
        Optional<TrainSchedule> existingOpt = trainScheduleService.getScheduleById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Ensure the ID is set correctly
        updated.setScheduleId(id);
        TrainSchedule saved = trainScheduleService.updateSchedule(updated);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrainSchedule>> searchSchedules(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String fromStation,
            @RequestParam(required = false) String toStation) {
        List<TrainSchedule> results;
        
        // If specific stations are provided, use station search
        if (fromStation != null || toStation != null) {
            results = trainScheduleService.findByStations(fromStation, toStation);
        }
        // Otherwise use keyword search
        else if (keyword != null && !keyword.trim().isEmpty()) {
            results = trainScheduleService.searchSchedules(keyword);
        }
        // If no parameters, return all schedules
        else {
            results = trainScheduleService.getAllSchedules();
        }
        
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable String id) {
        trainScheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }
}

package com.travelguider.backend.controller;

import com.travelguider.backend.entity.BusSchedule;
import com.travelguider.backend.service.BusScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bus-schedules")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class BusScheduleController {
    @Autowired
    private BusScheduleService busScheduleService;

    @PostMapping("")
    public ResponseEntity<BusSchedule> addSchedule(@RequestBody BusSchedule schedule) {
        BusSchedule saved = busScheduleService.addSchedule(schedule);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("")
    public List<BusSchedule> getAllSchedules() {
        return busScheduleService.getAllSchedules();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusSchedule> getScheduleById(@PathVariable String id) {
        Optional<BusSchedule> schedule = busScheduleService.getScheduleById(id);
        return schedule.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusSchedule> updateSchedule(@PathVariable String id, @RequestBody BusSchedule updated) {
        Optional<BusSchedule> existingOpt = busScheduleService.getScheduleById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        updated.setId(id);
        BusSchedule saved = busScheduleService.addSchedule(updated);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable String id) {
        busScheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }
}

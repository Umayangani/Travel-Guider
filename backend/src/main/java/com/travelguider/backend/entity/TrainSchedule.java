package com.travelguider.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "train_schedules")
public class TrainSchedule implements Serializable {
    @Id
    @Column(name = "schedule_id", length = 50)
    private String scheduleId;

    @Column(name = "train_name", length = 100)
    private String trainName;

    @Column(name = "from_station", length = 100)
    private String fromStation;

    @Column(name = "to_station", length = 100)
    private String toStation;

    @Column(name = "departure_time")
    private java.sql.Time departureTime;

    @Column(name = "arrival_time")
    private java.sql.Time arrivalTime;

    @Column(name = "duration")
    private java.sql.Time duration;

    // Getters and setters
    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }
    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }
    public String getFromStation() { return fromStation; }
    public void setFromStation(String fromStation) { this.fromStation = fromStation; }
    public String getToStation() { return toStation; }
    public void setToStation(String toStation) { this.toStation = toStation; }
    public java.sql.Time getDepartureTime() { return departureTime; }
    public void setDepartureTime(java.sql.Time departureTime) { this.departureTime = departureTime; }
    public java.sql.Time getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(java.sql.Time arrivalTime) { this.arrivalTime = arrivalTime; }
    public java.sql.Time getDuration() { return duration; }
    public void setDuration(java.sql.Time duration) { this.duration = duration; }
}

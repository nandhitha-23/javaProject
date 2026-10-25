package com.smartlab.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Lab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String labName;
    private String location;
    private int capacity;

    public Lab() {
    }

    public Lab(String labName, String location, int capacity) {
        this.labName = labName;
        this.location = location;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getRoomNumber() {
        return location != null && !location.isBlank() ? location : "Facility #" + (id != null ? id : "N/A");
    }

    public void setRoomNumber(String roomNumber) {
        if (this.location == null || this.location.isBlank()) {
            this.location = roomNumber;
        }
    }
}
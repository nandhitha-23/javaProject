package com.smartlab.demo;

import java.time.LocalDate;
import java.time.LocalTime;

import com.smartlab.demo.model.Lab;
import com.smartlab.demo.model.Student;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class LabAllocation {
    
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@ManyToOne
@JoinColumn(name = "student_id")
private Student student;

@ManyToOne
@JoinColumn(name = "lab_id")
private Lab lab;

private LocalDate allocationDate;

private LocalTime startTime;

private LocalTime endTime;

// Individual seat allocation
private Integer seatNumber;

// Default constructor
public LabAllocation() {
}

// Getters and setters
public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public Student getStudent() {
    return student;
}

public void setStudent(Student student) {
    this.student = student;
}

public Lab getLab() {
    return lab;
}

public void setLab(Lab lab) {
    this.lab = lab;
}

public LocalDate getAllocationDate() {
    return allocationDate;
}

public void setAllocationDate(LocalDate allocationDate) {
    this.allocationDate = allocationDate;
}

public LocalTime getStartTime() {
    return startTime;
}

public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
}

public LocalTime getEndTime() {
    return endTime;
}

public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
}

public Integer getSeatNumber() {
    return seatNumber;
}

public void setSeatNumber(Integer seatNumber) {
    this.seatNumber = seatNumber;
}

}

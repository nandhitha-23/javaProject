package com.smartlab.demo.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartlab.demo.model.Lab;
import com.smartlab.demo.LabAllocation;
import com.smartlab.demo.repository.LabAllocationRepository;
import com.smartlab.demo.repository.LabRepository;

@Controller
public class LabAvailabilityController {

    private final LabRepository labRepository;
    private final LabAllocationRepository allocationRepository;

    public LabAvailabilityController(
            LabRepository labRepository,
            LabAllocationRepository allocationRepository) {

        this.labRepository = labRepository;
        this.allocationRepository = allocationRepository;
    }

    @GetMapping("/lab-availability")
    public String checkAvailability(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            Model model) {

        model.addAttribute("searched", false);
        model.addAttribute("date", date);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);

        List<Lab> labs = labRepository.findAll();

        // If search fields are empty, display all labs
        if (date == null || date.isBlank()
                || startTime == null || startTime.isBlank()
                || endTime == null || endTime.isBlank()) {

            model.addAttribute("labs", labs);
            return "lab-availability";
        }

        try {
            LocalDate selectedDate = LocalDate.parse(date);
            LocalTime selectedStart = LocalTime.parse(startTime);
            LocalTime selectedEnd = LocalTime.parse(endTime);

            // Validate date
            if (selectedDate.isBefore(LocalDate.now())) {
                model.addAttribute(
                        "error",
                        "Please select today or a future date.");

                model.addAttribute("labs", List.of());
                return "lab-availability";
            }

            // Validate time
            if (!selectedEnd.isAfter(selectedStart)) {
                model.addAttribute(
                        "error",
                        "End time must be after start time.");

                model.addAttribute("labs", List.of());
                return "lab-availability";
            }

            // Get all allocations once
            List<LabAllocation> allocations =
                    allocationRepository.findAll();

            // A lab is available if at least one seat is free
            List<Lab> availableLabs = labs.stream()
                    .filter(lab -> {

                        // Check each seat in this lab
                        for (int seat = 1;
                                seat <= lab.getCapacity();
                                seat++) {

                            // FIX: Create a final copy for the lambda
                            final int currentSeat = seat;

                            boolean booked = allocations.stream()
                                    .anyMatch(allocation -> {

                                        if (allocation.getLab() == null
                                                || allocation.getLab().getId() == null
                                                || lab.getId() == null) {
                                            return false;
                                        }

                                        // Check lab ID
                                        if (!allocation.getLab().getId()
                                                .equals(lab.getId())) {
                                            return false;
                                        }

                                        // Validate allocation fields
                                        if (allocation.getAllocationDate() == null
                                                || allocation.getStartTime() == null
                                                || allocation.getEndTime() == null) {
                                            return false;
                                        }

                                        // Check selected date
                                        if (!allocation.getAllocationDate()
                                                .equals(selectedDate)) {
                                            return false;
                                        }

                                        // Check time overlap
                                        boolean timeOverlap = allocation.getStartTime()
                                                .isBefore(selectedEnd)
                                                && allocation.getEndTime()
                                                        .isAfter(selectedStart);
                                        if (!timeOverlap) {
                                            return false;
                                        }

                                        // If whole lab is allocated (seatNumber is null), all seats are booked
                                        if (allocation.getSeatNumber() == null) {
                                            return true;
                                        }

                                        // Check seat number
                                        return allocation.getSeatNumber().equals(currentSeat);
                                    });

                            // One free seat makes the lab available
                            if (!booked) {
                                return true;
                            }
                        }

                        // Every seat is booked
                        return false;
                    })
                    .toList();

            model.addAttribute("labs", availableLabs);
            model.addAttribute("searched", true);

        } catch (DateTimeParseException e) {

            model.addAttribute(
                    "error",
                    "Invalid date or time. Please select valid values.");

            model.addAttribute("labs", List.of());
        }

        return "lab-availability";
    }
}
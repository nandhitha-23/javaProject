package com.smartlab.demo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        List<Lab> availableLabs = labRepository.findAll();

        if (date != null && !date.isBlank()
                && startTime != null && !startTime.isBlank()
                && endTime != null && !endTime.isBlank()) {

            LocalDate selectedDate = LocalDate.parse(date);
            LocalTime selectedStart = LocalTime.parse(startTime);
            LocalTime selectedEnd = LocalTime.parse(endTime);

            if (!selectedEnd.isAfter(selectedStart)) {
                model.addAttribute(
                        "error",
                        "End time must be after start time.");

                model.addAttribute("labs", List.of());
                model.addAttribute("searched", false);

                return "lab-availability";
            }

            availableLabs = availableLabs.stream()
                    .filter(lab -> allocationRepository.findAll()
                            .stream()
                            .noneMatch(allocation ->
                                    allocation.getLab() != null
                                            && allocation.getLab().getId()
                                                    .equals(lab.getId())
                                            && allocation.getAllocationDate()
                                                    .equals(selectedDate)
                                            && allocation.getStartTime()
                                                    .isBefore(selectedEnd)
                                            && allocation.getEndTime()
                                                    .isAfter(selectedStart)))
                    .toList();

            model.addAttribute("searched", true);

        } else {
            model.addAttribute("searched", false);
        }

        model.addAttribute("labs", availableLabs);
        model.addAttribute("date", date);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);

        return "lab-availability";
    }
}
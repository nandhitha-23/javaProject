package com.smartlab.demo.controller;

import com.smartlab.demo.LabAllocation;
import com.smartlab.demo.model.Lab;
import com.smartlab.demo.repository.LabAllocationRepository;
import com.smartlab.demo.repository.LabRepository;
import com.smartlab.demo.model.Student;
import com.smartlab.demo.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Controller
public class StudentPortalController {

    private final StudentRepository studentRepository;
    private final LabAllocationRepository allocationRepository;
    private final LabRepository labRepository;

    public StudentPortalController(
            StudentRepository studentRepository,
            LabAllocationRepository allocationRepository,
            LabRepository labRepository) {
        this.studentRepository = studentRepository;
        this.allocationRepository = allocationRepository;
        this.labRepository = labRepository;
    }

    /**
     * Student Portal Landing Page - Roll Number Entry
     */
    @GetMapping({"/student", "/student/portal"})
    public String showPortal(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String rollNo,
            Model model) {

        model.addAttribute("error", error);
        model.addAttribute("enteredRollNo", rollNo);
        model.addAttribute("registeredStudents", studentRepository.findAll());

        return "student-portal";
    }

    /**
     * Form submission from Roll Number lookup form
     */
    @PostMapping("/student/portal")
    public String handlePortalLookup(@RequestParam("rollNo") String rollNo) {
        if (rollNo == null || rollNo.trim().isBlank()) {
            return "redirect:/student/portal?error=empty";
        }
        String cleanRoll = rollNo.trim();
        return "redirect:/student/dashboard?rollNo=" + URLEncoder.encode(cleanRoll, StandardCharsets.UTF_8);
    }

    /**
     * Student Personalized Dashboard
     */
    @GetMapping("/student/dashboard")
    public String studentDashboard(
            @RequestParam(value = "rollNo", required = false) String rollNo,
            Model model) {

        if (rollNo == null || rollNo.trim().isBlank()) {
            return "redirect:/student/portal";
        }

        String cleanRoll = rollNo.trim();
        Optional<Student> studentOpt = studentRepository.findByRollNo(cleanRoll);

        if (studentOpt.isEmpty()) {
            return "redirect:/student/portal?error=notfound&rollNo=" +
                    URLEncoder.encode(cleanRoll, StandardCharsets.UTF_8);
        }

        Student student = studentOpt.get();
        List<LabAllocation> allAllocations =
                allocationRepository.findByStudent_RollNoOrderByAllocationDateAscStartTimeAsc(student.getRollNo());

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<LabAllocation> todayAllocations = allAllocations.stream()
                .filter(a -> a.getAllocationDate().isEqual(today))
                .toList();

        List<LabAllocation> upcomingAllocations = allAllocations.stream()
                .filter(a -> a.getAllocationDate().isAfter(today)
                        || (a.getAllocationDate().isEqual(today) && a.getEndTime().isAfter(now)))
                .toList();

        List<LabAllocation> pastAllocations = allAllocations.stream()
                .filter(a -> a.getAllocationDate().isBefore(today)
                        || (a.getAllocationDate().isEqual(today) && a.getEndTime().isBefore(now)))
                .toList();

        model.addAttribute("student", student);
        model.addAttribute("rollNo", student.getRollNo());
        model.addAttribute("allocations", allAllocations);
        model.addAttribute("todayAllocations", todayAllocations);
        model.addAttribute("upcomingAllocations", upcomingAllocations);
        model.addAttribute("pastAllocations", pastAllocations);
        model.addAttribute("totalAllocationsCount", allAllocations.size());
        model.addAttribute("todayCount", todayAllocations.size());
        model.addAttribute("upcomingCount", upcomingAllocations.size());
        model.addAttribute("totalLabsCount", labRepository.count());
        model.addAttribute("todayDate", today);

        return "student-dashboard";
    }

    /**
     * Lab Availability Finder for Students
     */
    @GetMapping("/student/availability")
    public String checkStudentLabAvailability(
            @RequestParam(required = false) String rollNo,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            Model model) {

        List<Lab> labs = labRepository.findAll();
        boolean searched = false;

        if (date != null && !date.isBlank()
                && startTime != null && !startTime.isBlank()
                && endTime != null && !endTime.isBlank()) {

            try {
                LocalDate selectedDate = LocalDate.parse(date);
                LocalTime selectedStart = LocalTime.parse(startTime);
                LocalTime selectedEnd = LocalTime.parse(endTime);

                if (selectedDate.isBefore(LocalDate.now())) {
                    model.addAttribute("error", "Please select today or a future date.");
                    model.addAttribute("labs", List.of());
                } else if (!selectedEnd.isAfter(selectedStart)) {
                    model.addAttribute("error", "End time must be after start time.");
                    model.addAttribute("labs", List.of());
                } else {
                    List<LabAllocation> allocations = allocationRepository.findAll();

                    // A lab is available if at least one seat is free
                    List<Lab> availableLabs = labs.stream()
                            .filter(lab -> {
                                for (int seat = 1; seat <= lab.getCapacity(); seat++) {
                                    final int currentSeat = seat;

                                    boolean booked = allocations.stream().anyMatch(allocation -> {
                                        if (allocation.getLab() == null
                                                || allocation.getLab().getId() == null
                                                || !allocation.getLab().getId().equals(lab.getId())) {
                                            return false;
                                        }

                                        if (allocation.getAllocationDate() == null
                                                || !allocation.getAllocationDate().equals(selectedDate)) {
                                            return false;
                                        }

                                        if (allocation.getStartTime() == null || allocation.getEndTime() == null) {
                                            return false;
                                        }

                                        boolean timeOverlap = allocation.getStartTime().isBefore(selectedEnd)
                                                && allocation.getEndTime().isAfter(selectedStart);
                                        if (!timeOverlap) {
                                            return false;
                                        }

                                        // If whole lab is allocated (seatNumber is null), all seats are booked
                                        if (allocation.getSeatNumber() == null) {
                                            return true;
                                        }

                                        return allocation.getSeatNumber().equals(currentSeat);
                                    });

                                    // If at least one seat is free, the lab is available
                                    if (!booked) {
                                        return true;
                                    }
                                }
                                return false;
                            })
                            .toList();

                    model.addAttribute("labs", availableLabs);
                    searched = true;
                }

            } catch (DateTimeParseException e) {
                model.addAttribute("error", "Invalid date or time format. Please check your inputs.");
                model.addAttribute("labs", List.of());
            }

        } else {
            // When no filter searched, display all laboratories
            model.addAttribute("labs", labs);
        }

        model.addAttribute("rollNo", rollNo);
        model.addAttribute("searched", searched);
        model.addAttribute("date", date);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);

        // If rollNo is provided, attach student info for header
        if (rollNo != null && !rollNo.isBlank()) {
            studentRepository.findByRollNo(rollNo.trim())
                    .ifPresent(student -> model.addAttribute("student", student));
        }

        return "student-availability";
    }

    /**
     * Campus Laboratories Directory for Students
     */
    @GetMapping("/student/labs")
    public String listLabsForStudents(
            @RequestParam(required = false) String rollNo,
            Model model) {

        model.addAttribute("labs", labRepository.findAll());
        model.addAttribute("rollNo", rollNo);

        if (rollNo != null && !rollNo.isBlank()) {
            studentRepository.findByRollNo(rollNo.trim())
                    .ifPresent(student -> model.addAttribute("student", student));
        }

        return "student-labs";
    }
}

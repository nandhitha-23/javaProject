package com.smartlab.demo.controller;

import com.smartlab.demo.LabAllocation;
import com.smartlab.demo.model.Lab;
import com.smartlab.demo.model.Student;
import com.smartlab.demo.repository.LabAllocationRepository;
import com.smartlab.demo.repository.LabRepository;
import com.smartlab.demo.repository.StudentRepository;
import com.smartlab.demo.service.LabAllocationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Controller
public class LabAllocationController {

    private final LabAllocationService allocationService;
    private final LabAllocationRepository allocationRepository;
    private final StudentRepository studentRepository;
    private final LabRepository labRepository;

    public LabAllocationController(
            LabAllocationService allocationService,
            LabAllocationRepository allocationRepository,
            StudentRepository studentRepository,
            LabRepository labRepository) {

        this.allocationService = allocationService;
        this.allocationRepository = allocationRepository;
        this.studentRepository = studentRepository;
        this.labRepository = labRepository;
    }

    @GetMapping("/allocations")
    public String viewAllocations(Model model) {
        model.addAttribute(
                "allocations",
                allocationService.getAllAllocations()
        );
        return "allocations";
    }

    @GetMapping("/allocations/add")
    public String showAddForm(Model model) {
        model.addAttribute("allocation", new LabAllocation());
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("labs", labRepository.findAll());

        return "add-allocation";
    }

    @PostMapping("/allocations/save")
    public String saveAllocation(
            @ModelAttribute("allocation") LabAllocation allocation,
            RedirectAttributes redirectAttributes) {

        try {
            allocationService.saveAllocation(allocation);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Lab allocation saved successfully!"
            );
            return "redirect:/allocations";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
            return "redirect:/allocations/add";
        }
    }

    @GetMapping("/allocations/edit/{id}")
    public String editAllocation(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "allocation",
                allocationService.getAllocationById(id)
        );
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("labs", labRepository.findAll());

        return "add-allocation";
    }

    @GetMapping("/allocations/delete/{id}")
    public String deleteAllocation(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        allocationService.deleteAllocation(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Lab allocation deleted successfully!"
        );

        return "redirect:/allocations";
    }

    /**
     * Book individual seat in a laboratory (Admin & Student friendly)
     */
    @PostMapping("/book-seat")
    public String bookSeat(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String rollNo,
            @RequestParam Long labId,
            @RequestParam Integer seatNumber,
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(required = false) String source,
            RedirectAttributes redirectAttributes) {

        boolean isStudent = "student".equalsIgnoreCase(source) || (rollNo != null && !rollNo.isBlank());
        String redirectTarget = isStudent ? "/student/availability" : "/lab-availability";

        try {
            if (labId == null || seatNumber == null || seatNumber < 1) {
                redirectAttributes.addFlashAttribute("error", "Please select valid booking details.");
                return buildRedirect(redirectTarget, isStudent, rollNo, date, startTime, endTime);
            }

            LocalDate allocationDate = LocalDate.parse(date);
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            if (allocationDate.isBefore(LocalDate.now())) {
                redirectAttributes.addFlashAttribute("error", "Please select today or a future date.");
                return buildRedirect(redirectTarget, isStudent, rollNo, date, startTime, endTime);
            }

            if (!end.isAfter(start)) {
                redirectAttributes.addFlashAttribute("error", "End time must be after start time.");
                return buildRedirect(redirectTarget, isStudent, rollNo, date, startTime, endTime);
            }

            Student student = null;
            if (rollNo != null && !rollNo.trim().isBlank()) {
                student = studentRepository.findByRollNo(rollNo.trim()).orElse(null);
            }
            if (student == null && studentId != null) {
                student = studentRepository.findById(studentId).orElse(null);
            }

            Lab lab = labRepository.findById(labId).orElse(null);

            if (student == null) {
                redirectAttributes.addFlashAttribute("error", "Student not found. Please provide a valid Roll Number or Student ID.");
                return buildRedirect(redirectTarget, isStudent, rollNo, date, startTime, endTime);
            }

            if (lab == null) {
                redirectAttributes.addFlashAttribute("error", "Laboratory facility not found.");
                return buildRedirect(redirectTarget, isStudent, rollNo, date, startTime, endTime);
            }

            if (seatNumber > lab.getCapacity()) {
                redirectAttributes.addFlashAttribute("error", "Invalid seat number for this laboratory (Capacity: " + lab.getCapacity() + ").");
                return buildRedirect(redirectTarget, isStudent, rollNo, date, startTime, endTime);
            }

            boolean booked = allocationRepository.isSeatBooked(labId, seatNumber, allocationDate, start, end);
            if (booked) {
                redirectAttributes.addFlashAttribute("error", "Seat #" + seatNumber + " is already booked for the selected time.");
                return buildRedirect(redirectTarget, isStudent, rollNo, date, startTime, endTime);
            }

            LabAllocation allocation = new LabAllocation();
            allocation.setStudent(student);
            allocation.setLab(lab);
            allocation.setSeatNumber(seatNumber);
            allocation.setAllocationDate(allocationDate);
            allocation.setStartTime(start);
            allocation.setEndTime(end);

            allocationRepository.save(allocation);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Seat #" + seatNumber + " in " + lab.getLabName() + " booked successfully!");

        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid date or time format. Please check your inputs.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Booking failed: " + e.getMessage());
        }

        return buildRedirect(redirectTarget, isStudent, rollNo, date, startTime, endTime);
    }

    private String buildRedirect(String target, boolean isStudent, String rollNo, String date, String startTime, String endTime) {
        StringBuilder sb = new StringBuilder("redirect:").append(target).append("?");
        boolean hasParam = false;
        if (isStudent && rollNo != null && !rollNo.isBlank()) {
            sb.append("rollNo=").append(URLEncoder.encode(rollNo.trim(), StandardCharsets.UTF_8));
            hasParam = true;
        }
        if (date != null && !date.isBlank()) {
            if (hasParam) sb.append("&");
            sb.append("date=").append(URLEncoder.encode(date.trim(), StandardCharsets.UTF_8));
            hasParam = true;
        }
        if (startTime != null && !startTime.isBlank()) {
            if (hasParam) sb.append("&");
            sb.append("startTime=").append(URLEncoder.encode(startTime.trim(), StandardCharsets.UTF_8));
            hasParam = true;
        }
        if (endTime != null && !endTime.isBlank()) {
            if (hasParam) sb.append("&");
            sb.append("endTime=").append(URLEncoder.encode(endTime.trim(), StandardCharsets.UTF_8));
            hasParam = true;
        }
        return sb.toString();
    }
}
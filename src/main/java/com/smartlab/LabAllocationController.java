package com.smartlab.demo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartlab.demo.model.Student;
import com.smartlab.demo.repository.StudentRepository;

@Controller
public class LabAllocationController {

    private final LabRepository labRepository;
    private final LabAllocationRepository allocationRepository;
    private final StudentRepository studentRepository;

    public LabAllocationController(
            LabRepository labRepository,
            LabAllocationRepository allocationRepository,
            StudentRepository studentRepository) {

        this.labRepository = labRepository;
        this.allocationRepository = allocationRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/book-seat")
    public String bookSeat(
            @RequestParam Long studentId,
            @RequestParam Long labId,
            @RequestParam Integer seatNumber,
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime,
            RedirectAttributes redirectAttributes) {

        try {
            // Validate the booking details
            if (studentId == null || labId == null
                    || seatNumber == null || seatNumber < 1) {

                redirectAttributes.addFlashAttribute(
                        "error", "Please select valid booking details.");

                return "redirect:/lab-availability";
            }

            LocalDate allocationDate = LocalDate.parse(date);
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            // Date must be today or in the future
            if (allocationDate.isBefore(LocalDate.now())) {

                redirectAttributes.addFlashAttribute(
                        "error", "Please select today or a future date.");

                return "redirect:/lab-availability";
            }

            // End time must be after start time
            if (!end.isAfter(start)) {

                redirectAttributes.addFlashAttribute(
                        "error", "End time must be after start time.");

                return "redirect:/lab-availability";
            }

            // Find student
            Student student = studentRepository
                    .findById(studentId)
                    .orElse(null);

            // Find laboratory
            Lab lab = labRepository
                    .findById(labId)
                    .orElse(null);

            if (student == null || lab == null) {

                redirectAttributes.addFlashAttribute(
                        "error", "Student or laboratory not found.");

                return "redirect:/lab-availability";
            }

            // Check whether the seat number is valid
            if (seatNumber > lab.getCapacity()) {

                redirectAttributes.addFlashAttribute(
                        "error", "Invalid seat number for this laboratory.");

                return "redirect:/lab-availability";
            }

            // Check whether the selected seat is already booked
            boolean booked = allocationRepository.isSeatBooked(
                    labId,
                    seatNumber,
                    allocationDate,
                    start,
                    end
            );

            if (booked) {

                redirectAttributes.addFlashAttribute(
                        "error",
                        "This seat is already booked for the selected time.");

                return "redirect:/lab-availability";
            }

            // Create a new allocation
            LabAllocation allocation = new LabAllocation();

            allocation.setStudent(student);
            allocation.setLab(lab);
            allocation.setSeatNumber(seatNumber);
            allocation.setAllocationDate(allocationDate);
            allocation.setStartTime(start);
            allocation.setEndTime(end);

            // Save booking to the database
            allocationRepository.save(allocation);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Seat " + seatNumber + " booked successfully!");

        } catch (DateTimeParseException e) {

            redirectAttributes.addFlashAttribute(
                    "error", "Invalid date or time format.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error", "Booking failed. Please check the details.");

        }

        return "redirect:/lab-availability";
    }
}
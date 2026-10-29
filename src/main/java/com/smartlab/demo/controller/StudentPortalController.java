
package com.smartlab.demo.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartlab.demo.LabAllocation;
import com.smartlab.demo.model.Lab;
import com.smartlab.demo.model.Student;
import com.smartlab.demo.repository.LabAllocationRepository;
import com.smartlab.demo.repository.LabRepository;
import com.smartlab.demo.repository.StudentRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class StudentPortalController {

    private static final String STUDENT_SESSION_KEY =
            "authenticatedStudentRollNo";

    private final StudentRepository studentRepository;
    private final LabAllocationRepository allocationRepository;
    private final LabRepository labRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentPortalController(
            StudentRepository studentRepository,
            LabAllocationRepository allocationRepository,
            LabRepository labRepository,
            PasswordEncoder passwordEncoder) {

        this.studentRepository = studentRepository;
        this.allocationRepository = allocationRepository;
        this.labRepository = labRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * Student login page
     * Registered student details are not sent to this page.
     */
    @GetMapping({"/student", "/student/portal"})
    public String showPortal(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String rollNo,
            Model model,
            HttpSession session) {

        // If a student is already logged in, open their dashboard.
        String authenticatedRollNo =
                (String) session.getAttribute(STUDENT_SESSION_KEY);

        if (authenticatedRollNo != null) {
            return "redirect:/student/dashboard";
        }

        model.addAttribute("error", error);
        model.addAttribute("enteredRollNo", rollNo);

        return "student-portal";
    }

    /*
     * Authenticate student using roll number and password.
     */
    @PostMapping("/student/portal")
    public String handlePortalLookup(
            @RequestParam("rollNo") String rollNo,
            @RequestParam("password") String password,
            HttpSession session) {

        if (rollNo == null || rollNo.isBlank()
                || password == null || password.isBlank()) {

            return "redirect:/student/portal?error=empty";
        }

        String cleanRollNo = rollNo.trim();

        Optional<Student> studentOptional =
                studentRepository.findByRollNo(cleanRollNo);

        if (studentOptional.isEmpty()) {
            return "redirect:/student/portal?error=invalid";
        }

        Student student = studentOptional.get();

        /*
         * Accounts without a password cannot log in.
         * Passwords must be stored as BCrypt hashes.
         */
        if (student.getPassword() == null
                || student.getPassword().isBlank()
                || !passwordEncoder.matches(
                        password,
                        student.getPassword())) {

            return "redirect:/student/portal?error=invalid";
        }

        /*
         * Change the session ID after successful authentication
         * to help prevent session fixation.
         */
        session.setAttribute(STUDENT_SESSION_KEY, student.getRollNo());

        return "redirect:/student/dashboard";
    }

    /*
     * Student dashboard
     * Identity is obtained from the session, not from a URL parameter.
     */
    @GetMapping("/student/dashboard")
    public String studentDashboard(
            HttpSession session,
            Model model) {

        String authenticatedRollNo = getAuthenticatedRollNo(session);

        if (authenticatedRollNo == null) {
            return "redirect:/student/portal";
        }

        Optional<Student> studentOptional =
                studentRepository.findByRollNo(authenticatedRollNo);

        if (studentOptional.isEmpty()) {
            session.removeAttribute(STUDENT_SESSION_KEY);
            return "redirect:/student/portal?error=invalid";
        }

        Student student = studentOptional.get();

        List<LabAllocation> allAllocations =
                allocationRepository
                        .findByStudent_RollNoOrderByAllocationDateAscStartTimeAsc(
                                student.getRollNo());

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<LabAllocation> todayAllocations =
                allAllocations.stream()
                        .filter(a -> a.getAllocationDate() != null
                                && a.getAllocationDate().isEqual(today))
                        .toList();

        List<LabAllocation> upcomingAllocations =
                allAllocations.stream()
                        .filter(a -> a.getAllocationDate() != null
                                && (a.getAllocationDate().isAfter(today)
                                || (a.getAllocationDate().isEqual(today)
                                && a.getEndTime() != null
                                && a.getEndTime().isAfter(now))))
                        .toList();

        List<LabAllocation> pastAllocations =
                allAllocations.stream()
                        .filter(a -> a.getAllocationDate() != null
                                && (a.getAllocationDate().isBefore(today)
                                || (a.getAllocationDate().isEqual(today)
                                && a.getEndTime() != null
                                && a.getEndTime().isBefore(now))))
                        .toList();

        /*
         * Do not send the stored password hash to the view model.
         */
        student.setPassword(null);

        model.addAttribute("student", student);
        model.addAttribute("rollNo", authenticatedRollNo);
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

    /*
     * Student lab availability page
     * Only the authenticated student's session is used for identity.
     */
    @GetMapping("/student/availability")
    public String checkStudentLabAvailability(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            HttpSession session,
            Model model) {

        String authenticatedRollNo = getAuthenticatedRollNo(session);

        if (authenticatedRollNo == null) {
            return "redirect:/student/portal";
        }

        Optional<Student> studentOptional =
                studentRepository.findByRollNo(authenticatedRollNo);

        if (studentOptional.isEmpty()) {
            session.removeAttribute(STUDENT_SESSION_KEY);
            return "redirect:/student/portal?error=invalid";
        }

        Student student = studentOptional.get();
        student.setPassword(null);

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

                    model.addAttribute(
                            "error",
                            "Please select today or a future date.");

                    model.addAttribute("labs", List.of());

                } else if (!selectedEnd.isAfter(selectedStart)) {

                    model.addAttribute(
                            "error",
                            "End time must be after start time.");

                    model.addAttribute("labs", List.of());

                } else {

                    List<LabAllocation> allocations =
                            allocationRepository.findAll();

                    /*
                     * A lab is available if at least one seat is free
                     * for the selected date and time.
                     */
                    List<Lab> availableLabs = labs.stream()
                            .filter(lab -> {

                                for (int seat = 1;
                                     seat <= lab.getCapacity();
                                     seat++) {

                                    final int currentSeat = seat;

                                    boolean booked =
                                            allocations.stream().anyMatch(
                                                    allocation -> {

                                                if (allocation.getLab() == null
                                                        || allocation.getLab().getId() == null
                                                        || lab.getId() == null
                                                        || !allocation.getLab()
                                                        .getId()
                                                        .equals(lab.getId())) {
                                                    return false;
                                                }

                                                if (allocation.getAllocationDate() == null
                                                        || !allocation.getAllocationDate()
                                                        .equals(selectedDate)) {
                                                    return false;
                                                }

                                                if (allocation.getStartTime() == null
                                                        || allocation.getEndTime() == null) {
                                                    return false;
                                                }

                                                boolean timeOverlap =
                                                        allocation.getStartTime()
                                                                .isBefore(selectedEnd)
                                                        && allocation.getEndTime()
                                                                .isAfter(selectedStart);

                                                if (!timeOverlap) {
                                                    return false;
                                                }

                                                /*
                                                 * If seatNumber is null,
                                                 * the whole lab is allocated.
                                                 */
                                                if (allocation.getSeatNumber() == null) {
                                                    return true;
                                                }

                                                return allocation.getSeatNumber()
                                                        .equals(currentSeat);
                                            });

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

                model.addAttribute(
                        "error",
                        "Invalid date or time format. Please check your inputs.");

                model.addAttribute("labs", List.of());
            }

        } else {
            model.addAttribute("labs", labs);
        }

        model.addAttribute("student", student);
        model.addAttribute("rollNo", authenticatedRollNo);
        model.addAttribute("searched", searched);
        model.addAttribute("date", date);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);

        return "student-availability";
    }

    /*
     * Student laboratories directory
     */
    @GetMapping("/student/labs")
    public String listLabsForStudents(
            HttpSession session,
            Model model) {

        String authenticatedRollNo = getAuthenticatedRollNo(session);

        if (authenticatedRollNo == null) {
            return "redirect:/student/portal";
        }

        Optional<Student> studentOptional =
                studentRepository.findByRollNo(authenticatedRollNo);

        if (studentOptional.isEmpty()) {
            session.removeAttribute(STUDENT_SESSION_KEY);
            return "redirect:/student/portal?error=invalid";
        }

        Student student = studentOptional.get();
        student.setPassword(null);

        model.addAttribute("labs", labRepository.findAll());
        model.addAttribute("student", student);
        model.addAttribute("rollNo", authenticatedRollNo);

        return "student-labs";
    }

    /*
     * Student logout
     */
    @PostMapping("/student/logout")
    public String studentLogout(HttpSession session) {

        session.invalidate();

        return "redirect:/student/portal?logout";
    }

    /*
     * Return the authenticated student's roll number from the session.
     */
    private String getAuthenticatedRollNo(HttpSession session) {

        Object value = session.getAttribute(STUDENT_SESSION_KEY);

        if (value instanceof String rollNo && !rollNo.isBlank()) {
            return rollNo;
        }

        return null;
    }
}

package com.smartlab.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.smartlab.demo.repository.LabAllocationRepository;
import com.smartlab.demo.repository.LabRepository;
import com.smartlab.demo.repository.StudentRepository;

@Controller
public class DashboardController {

    private final StudentRepository studentRepository;
    private final LabRepository labRepository;
    private final LabAllocationRepository allocationRepository;

    public DashboardController(
            StudentRepository studentRepository,
            LabRepository labRepository,
            LabAllocationRepository allocationRepository) {

        this.studentRepository = studentRepository;
        this.labRepository = labRepository;
        this.allocationRepository = allocationRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {

        model.addAttribute(
                "totalStudents", studentRepository.count());

        model.addAttribute(
                "totalLabs", labRepository.count());

        model.addAttribute(
                "totalAllocations", allocationRepository.count());

        return "dashboard";
    }
}
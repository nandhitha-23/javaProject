package com.smartlab.demo;

import com.smartlab.demo.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class LabAllocationController {

    private final LabAllocationService allocationService;
    private final StudentRepository studentRepository;
    private final LabRepository labRepository;

    public LabAllocationController(
            LabAllocationService allocationService,
            StudentRepository studentRepository,
            LabRepository labRepository) {

        this.allocationService = allocationService;
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
}
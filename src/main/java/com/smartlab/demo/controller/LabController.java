package com.smartlab.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartlab.demo.model.Lab;
import com.smartlab.demo.service.LabService;

@Controller
public class LabController {

    private final LabService labService;

    public LabController(LabService labService) {
        this.labService = labService;
    }

    @GetMapping("/labs")
    public String viewLabs(Model model) {
        model.addAttribute("labs", labService.getAllLabs());
        return "labs";
    }

    @GetMapping("/labs/add")
    public String showAddLabForm(Model model) {
        model.addAttribute("lab", new Lab());
        return "add-lab";
    }

    @PostMapping("/labs/save")
    public String saveLab(@ModelAttribute Lab lab,
                          RedirectAttributes redirectAttributes) {
        labService.saveLab(lab);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Lab saved successfully!");
        return "redirect:/labs";
    }

    @GetMapping("/labs/edit/{id}")
    public String editLab(@PathVariable Long id, Model model) {
        model.addAttribute("lab", labService.getLabById(id));
        return "add-lab";
    }

    @GetMapping("/labs/delete/{id}")
    public String deleteLab(@PathVariable Long id,
                            RedirectAttributes redirectAttributes) {
        labService.deleteLab(id);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Lab deleted successfully!");
        return "redirect:/labs";
    }
}
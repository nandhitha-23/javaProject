package com.smartlab.demo.controller;

import com.smartlab.demo.model.Student;
import com.smartlab.demo.service.StudentService;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students")
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("student", new Student());
        return "students";
    }

    @PostMapping("/students/add")
public String addStudent(
        @Valid @ModelAttribute("student") Student student,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }

    try {
        studentService.addStudent(student);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Student added successfully!");
    } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute(
                "errorMessage", e.getMessage());
    }

    return "redirect:/students";
}


// UPDATE STUDENT - EDIT PAGE
@GetMapping("/students/edit/{id}")
public String editStudent(@PathVariable Long id, Model model) {
    Student student = studentService.getStudentById(id);
    model.addAttribute("student", student);
    return "edit-student";
}


// UPDATE STUDENT - SAVE CHANGES
@PostMapping("/students/update")
public String updateStudent(
        @Valid @ModelAttribute("student") Student student,
        BindingResult result,
        RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        return "edit-student";
    }

    try {
        studentService.updateStudent(student.getStudentId(), student);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Student updated successfully!");
    } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute(
                "errorMessage", e.getMessage());
    }

    return "redirect:/students";
}
    @PostMapping("/students/delete/{id}")
    public String deleteStudent(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Student deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", e.getMessage());
        }

        return "redirect:/students";
    }
}
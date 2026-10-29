
package com.smartlab.demo.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smartlab.demo.model.Student;
import com.smartlab.demo.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Add a new student with an encrypted password hash
    public Student addStudent(Student student) {

        if (studentRepository.existsByRollNo(student.getRollNo())) {
            throw new IllegalArgumentException("Roll number already exists!");
        }

        if (student.getPassword() == null
                || student.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required!");
        }

        student.setPassword(
                passwordEncoder.encode(student.getPassword())
        );

        return studentRepository.save(student);
    }

    // Get all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get student by ID
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found with ID: " + id
                        ));
    }

    // Update student details without overwriting the existing password
    public Student updateStudent(Long id, Student updatedStudent) {

        Student existingStudent = getStudentById(id);

        existingStudent.setStudentName(updatedStudent.getStudentName());
        existingStudent.setRollNo(updatedStudent.getRollNo());
        existingStudent.setDepartment(updatedStudent.getDepartment());

        // Keep the existing password unchanged during profile updates.
        return studentRepository.save(existingStudent);
    }

    // Delete student
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }
}

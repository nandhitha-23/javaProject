package com.smartlab.demo;
import com.smartlab.demo.model.Student;
import com.smartlab.demo.repository.StudentRepository;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabAllocationService {

    private final LabAllocationRepository allocationRepository;
    private final StudentRepository studentRepository;
    private final LabRepository labRepository;

    public LabAllocationService(
            LabAllocationRepository allocationRepository,
            StudentRepository studentRepository,
            LabRepository labRepository) {

        this.allocationRepository = allocationRepository;
        this.studentRepository = studentRepository;
        this.labRepository = labRepository;
    }

    public List<LabAllocation> getAllAllocations() {
        return allocationRepository.findAll();
    }

    public LabAllocation getAllocationById(Long id) {
        return allocationRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Allocation not found with ID: " + id));
    }

    public LabAllocation saveAllocation(LabAllocation allocation) {

        if (allocation.getStudent() == null
                || allocation.getStudent().getStudentId()== null) {
            throw new IllegalArgumentException("Please select a student.");
        }

        if (allocation.getLab() == null
                || allocation.getLab().getId() == null) {
            throw new IllegalArgumentException("Please select a lab.");
        }

        if (allocation.getAllocationDate() == null
                || allocation.getStartTime() == null
                || allocation.getEndTime() == null) {
            throw new IllegalArgumentException(
                    "Please provide the date and both times.");
        }

        if (!allocation.getEndTime().isAfter(allocation.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time.");
        }

        Long studentId =allocation.getStudent().getStudentId();
        Long labId = allocation.getLab().getId();

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found."));

        Lab lab = labRepository.findById(labId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Lab not found."));

        allocation.setStudent(student);
        allocation.setLab(lab);

        List<LabAllocation> existingAllocations =
                allocationRepository.findAll();

        for (LabAllocation existing : existingAllocations) {

            if (allocation.getId() != null
                    && allocation.getId().equals(existing.getId())) {
                continue;
            }

            boolean sameLab =
                    existing.getLab().getId().equals(labId);

            boolean sameDate =
                    existing.getAllocationDate()
                            .equals(allocation.getAllocationDate());

            boolean timeOverlap =
                    allocation.getStartTime()
                            .isBefore(existing.getEndTime())
                    && allocation.getEndTime()
                            .isAfter(existing.getStartTime());

            if (sameLab && sameDate && timeOverlap) {
                throw new IllegalArgumentException(
                        "This lab is already allocated during that time.");
            }
        }

        return allocationRepository.save(allocation);
    }

    public void deleteAllocation(Long id) {
        if (!allocationRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Allocation not found with ID: " + id);
        }

        allocationRepository.deleteById(id);
    }
}

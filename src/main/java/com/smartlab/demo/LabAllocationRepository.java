package com.smartlab.demo;

import com.smartlab.demo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabAllocationRepository
        extends JpaRepository<LabAllocation, Long> {

    List<LabAllocation> findByStudentOrderByAllocationDateAscStartTimeAsc(Student student);

    List<LabAllocation> findByStudent_RollNoOrderByAllocationDateAscStartTimeAsc(String rollNo);

    List<LabAllocation> findByStudent_StudentIdOrderByAllocationDateAscStartTimeAsc(Long studentId);
}

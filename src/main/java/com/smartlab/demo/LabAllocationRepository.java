package com.smartlab.demo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartlab.demo.model.Student;

public interface LabAllocationRepository
    extends JpaRepository<LabAllocation, Long> {

  List<LabAllocation> findByStudentOrderByAllocationDateAscStartTimeAsc(Student student);

  List<LabAllocation> findByStudent_RollNoOrderByAllocationDateAscStartTimeAsc(String rollNo);

  List<LabAllocation> findByStudent_StudentIdOrderByAllocationDateAscStartTimeAsc(Long studentId);

  @Query("""
      SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
      FROM LabAllocation a
      WHERE a.lab.id = :labId
        AND (a.seatNumber = :seatNumber OR a.seatNumber IS NULL)
        AND a.allocationDate = :allocationDate
        AND a.startTime < :endTime
        AND a.endTime > :startTime
      """)
  boolean isSeatBooked(
      @Param("labId") Long labId,
      @Param("seatNumber") Integer seatNumber,
      @Param("allocationDate") LocalDate allocationDate,
      @Param("startTime") LocalTime startTime,
      @Param("endTime") LocalTime endTime);
}
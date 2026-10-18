package com.smartlab.demo;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LabAllocationRepository
        extends JpaRepository<LabAllocation, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM LabAllocation a
        WHERE a.lab.id = :labId
          AND a.seatNumber = :seatNumber
          AND a.allocationDate = :allocationDate
          AND a.startTime < :endTime
          AND a.endTime > :startTime
        """)
    boolean isSeatBooked(
            @Param("labId") Long labId,
            @Param("seatNumber") Integer seatNumber,
            @Param("allocationDate") LocalDate allocationDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
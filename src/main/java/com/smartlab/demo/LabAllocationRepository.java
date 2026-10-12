package com.smartlab.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LabAllocationRepository
        extends JpaRepository<LabAllocation, Long> {
}

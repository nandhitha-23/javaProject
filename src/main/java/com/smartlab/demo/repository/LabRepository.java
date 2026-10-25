package com.smartlab.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartlab.demo.model.Lab;

public interface LabRepository extends JpaRepository<Lab, Long> {

}

package com.smartlab.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartlab.demo.model.Lab;
import com.smartlab.demo.repository.LabRepository;

@Service
public class LabService {

    private final LabRepository labRepository;

    public LabService(LabRepository labRepository) {
        this.labRepository = labRepository;
    }

    public List<Lab> getAllLabs() {
        return labRepository.findAll();
    }

    public Lab getLabById(Long id) {
        return labRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Lab not found with ID: " + id));
    }

    public Lab saveLab(Lab lab) {
        return labRepository.save(lab);
    }

    public void deleteLab(Long id) {
        labRepository.deleteById(id);
    }
}
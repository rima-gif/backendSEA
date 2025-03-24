package com.rima.ryma_prj.application.service;

import com.rima.ryma_prj.domain.model.RadioFrequency;
import com.rima.ryma_prj.domain.repository.RadioFrequencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // Assurez-vous que la classe est annotée avec @Service
public class RadioFrequenceService {

    private final RadioFrequencyRepository radioFrequencyRepository;

    // Constructeur public pour l'injection de dépendances
    @Autowired
    public RadioFrequenceService(RadioFrequencyRepository radioFrequencyRepository) {
        this.radioFrequencyRepository = radioFrequencyRepository;
    }

    public List<RadioFrequency> getAllRadioFrequencies() {
        return radioFrequencyRepository.findAll();
    }

    public Optional<RadioFrequency> getRadioFrequencyById(Long id) {
        return radioFrequencyRepository.findById(id);
    }

    public RadioFrequency createRadioFrequency(RadioFrequency radioFrequency) {
        if (radioFrequency.getUID() == null || !radioFrequency.getUID().matches("\\d{8}")) {
            throw new IllegalArgumentException("L'UID doit contenir exactement 8 chiffres.");
        }

        return radioFrequencyRepository.save(radioFrequency);
    }
    @Transactional
    public Optional<RadioFrequency> updateRadioFrequency(Long id, RadioFrequency updatedRadioFrequency) {
        return radioFrequencyRepository.findById(id).map(existing -> {
            existing.setUID(updatedRadioFrequency.getUID());
            return radioFrequencyRepository.save(existing);
        });
    }

    @Transactional
    public boolean deleteRadioFrequency(Long id) {
        if (radioFrequencyRepository.existsById(id)) {
            radioFrequencyRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
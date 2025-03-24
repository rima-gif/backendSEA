package com.rima.ryma_prj.application.service;

import com.rima.ryma_prj.domain.model.RadioFrequency;
import com.rima.ryma_prj.domain.model.machine;
import com.rima.ryma_prj.domain.repository.MachineRepository;
import com.rima.ryma_prj.domain.repository.RadioFrequencyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class MachineService {
    private  final MachineRepository machineRepository;
    private final RadioFrequencyRepository radioFrequencyRepository;

    public MachineService(MachineRepository machineRepository, RadioFrequencyRepository radioFrequencyRepository) {
        this.machineRepository = machineRepository;
        this.radioFrequencyRepository = radioFrequencyRepository;
    }

    public List<machine> getAllMachines() {
        return machineRepository.findAll();
    }

    public Optional<machine> getMachineById(Long id) {
        return machineRepository.findById(id);
    }

    public machine createMachine(machine machine) {
        return machineRepository.save(machine);
    }

    public machine saveMachine(machine machine) {
        return machineRepository.save(machine);
    }

    public void deleteMachineById(Long id) {
        machineRepository.deleteById(id);
    }

    // ✅ Ajout de la méthode qui manquait
    public Optional<RadioFrequency> findRadioFrequencyById(Long id) {
        return radioFrequencyRepository.findById(id);
    }
}
package com.rima.ryma_prj.infrastructure.controller;

import com.rima.ryma_prj.application.service.MachineService;
import com.rima.ryma_prj.domain.model.machine;
import com.rima.ryma_prj.domain.model.RadioFrequency;
import com.rima.ryma_prj.domain.repository.MachineRepository;
import com.rima.ryma_prj.domain.repository.RadioFrequencyRepository;
import com.rima.ryma_prj.infrastructure.DTO.MachineDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/machines")

public class MachineController {

    private final MachineRepository machineRepository;
    private final RadioFrequencyRepository radioFrequencyRepository;
    private final MachineService machineService;

    @Autowired
    public MachineController(MachineRepository machineRepository,
                             RadioFrequencyRepository radioFrequencyRepository,
                             MachineService machineService) {
        this.machineRepository = machineRepository;
        this.radioFrequencyRepository = radioFrequencyRepository;
        this.machineService = machineService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<machine>> getAllMachines() {
        List<machine> machines = machineService.getAllMachines();
        return ResponseEntity.ok(machines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<machine> getMachineById(@PathVariable Long id) {
        Optional<machine> machine = machineService.getMachineById(id);
        return machine.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> createMachine(@RequestBody machine machine) {

        Optional<RadioFrequency> radioFrequencyOpt = radioFrequencyRepository.findById(machine.getRadioFrequency().getId());

        if (radioFrequencyOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Erreur : Cette fréquence radio n'existe pas.");
        }

        Optional<machine> existingMachine = machineRepository.findByRadioFrequencyId(machine.getRadioFrequency().getId());

        if (existingMachine.isPresent()) {
            return ResponseEntity.badRequest().body("Erreur : L'ID " + machine.getRadioFrequency().getId() + " est déjà attribué à une autre machine.");
        }
        machine.setRadioFrequency(radioFrequencyOpt.get());
        machine savedMachine = machineRepository.save(machine);
        return ResponseEntity.ok(savedMachine);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> updateMachine(@PathVariable Long id, @RequestBody MachineDTO machineDTO) {
        Optional<machine> existingMachineOpt = machineService.getMachineById(id);

        if (existingMachineOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Erreur : La machine avec l'ID " + id + " n'existe pas.");
        }

        machine machine = existingMachineOpt.get();

        if (machineDTO.getName() != null && !machineDTO.getName().isEmpty()) {
            machine.setName(machineDTO.getName());
        }

        if (machineDTO.getRadioFrequencyId() != null) {
            Optional<RadioFrequency> newFrequencyOpt = radioFrequencyRepository.findById(machineDTO.getRadioFrequencyId());

            if (newFrequencyOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Erreur : Cette fréquence radio n'existe pas.");
            }

            Optional<machine> otherMachineWithSameFrequency = machineRepository.findByRadioFrequencyId(machineDTO.getRadioFrequencyId());

            if (otherMachineWithSameFrequency.isPresent() && !otherMachineWithSameFrequency.get().getId().equals(id)) {
                return ResponseEntity.badRequest().body("Erreur : La fréquence radio ID " + machineDTO.getRadioFrequencyId() + " est déjà attribuée à une autre machine.");
            }

            machine.setRadioFrequency(newFrequencyOpt.get());
        }

        machine updatedMachine = machineService.saveMachine(machine);
        return ResponseEntity.ok(updatedMachine);
    }
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> deleteMachine(@PathVariable Long id) {
        Optional<machine> existingMachineOpt = machineService.getMachineById(id);

        if (existingMachineOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Erreur : La machine avec l'ID " + id + " n'existe pas.");
        }

        machineService.deleteMachineById(id);
        return ResponseEntity.ok("Machine avec l'ID " + id + " a été supprimée avec succès.");
    }
}

package com.rima.ryma_prj.infrastructure.controller;

import com.rima.ryma_prj.application.service.MissionService;
import com.rima.ryma_prj.domain.model.Mission;
import com.rima.ryma_prj.domain.model.MissionStatus;
import com.rima.ryma_prj.domain.model.Robot;
import com.rima.ryma_prj.domain.model.machine;
import com.rima.ryma_prj.domain.repository.MachineRepository;
import com.rima.ryma_prj.domain.repository.MissionRepository;
import com.rima.ryma_prj.domain.repository.RobotRepository;
import com.rima.ryma_prj.infrastructure.DTO.MissionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mission")
public class MissionController {
    @Autowired
    private  MissionService missionService;
    @Autowired
    private RobotRepository robotRepository;
    @Autowired
    private MachineRepository machineRepository;
    @Autowired
    private MissionRepository missionRepository;


    public MissionController(MissionService missionService){
        this.missionService=missionService;
    }

    // Créer une mission (accessible uniquement aux SUPERADMIN)
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public Mission createMission(@RequestBody MissionRequest request) {
        // Récupérer le robot à partir de l'ID
        Robot robot = robotRepository.findById(request.getRobotId())
                .orElseThrow(() -> new RuntimeException("Robot non trouvé avec l'ID : " + request.getRobotId()));

        // Récupérer la liste des machines à partir des IDs
        List<machine> machines = machineRepository.findAllById(request.getMachineIds());

        return missionService.createMission(request.getNom(), robot, machines);
    }
//Demarrer  la mission
@PostMapping("/{missionId}/start")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public void startMission(@PathVariable Long missionId){
        missionService.startmission(missionId);
    }
    //continuer la mission
    @PostMapping("/{missionId}/next-step")
     @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
     public void nextStep(@PathVariable Long missionId) {
         missionService.nextStep(missionId);
     }
     //supprimer mission
     @DeleteMapping("/{missionId}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> deleteMission(@PathVariable Long missionId) {
        if (!missionRepository.existsById(missionId)) {
            return ResponseEntity.notFound().build();
        }
        missionService.deleteMission(missionId);
        return ResponseEntity.ok("Mission supprimée avec succès !");
    }
    @PutMapping("/{missionId}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Mission> updateMission(@PathVariable Long missionId, @RequestBody MissionRequest request) {
        if (!missionRepository.existsById(missionId)) {
            return ResponseEntity.notFound().build();
        }

        Robot robot = robotRepository.findById(request.getRobotId())
                .orElseThrow(() -> new RuntimeException("Robot non trouvé avec l'ID : " + request.getRobotId()));

        List<machine> machines = machineRepository.findAllById(request.getMachineIds());

        Mission updatedMission = missionService.updateMission(missionId, request.getNom(), robot, machines);
        return ResponseEntity.ok(updatedMission);
    }


}




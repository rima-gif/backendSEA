package com.rima.ryma_prj.infrastructure.controller;

import com.rima.ryma_prj.application.service.MissionService;
import com.rima.ryma_prj.domain.model.*;
import com.rima.ryma_prj.domain.repository.MachineRepository;
import com.rima.ryma_prj.domain.repository.MissionRepository;
import com.rima.ryma_prj.domain.repository.RobotRepository;
import com.rima.ryma_prj.infrastructure.DTO.MissionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mission")
public class MissionController {
    @Autowired
    private MissionService missionService;
    @Autowired
    private RobotRepository robotRepository;
    @Autowired
    private MachineRepository machineRepository;
    @Autowired
    private MissionRepository missionRepository;


    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    // Créer une mission (accessible uniquement aux SUPERADMIN)
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> createMission(@RequestBody MissionRequest request) {

        // Vérification des champs obligatoires
        if (request.getRobotId() == null) {
            return ResponseEntity.badRequest().body("Erreur : L'ID du robot est requis.");
        }
        if (request.getMachineIds() == null || request.getMachineIds().isEmpty()) {
            return ResponseEntity.badRequest().body("Erreur : Au moins une machine est requise.");
        }

        // Récupérer le robot
        Optional<Robot> robotOpt = robotRepository.findById(request.getRobotId());
        if (robotOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Erreur : Robot non trouvé avec l'ID " + request.getRobotId());
        }
        Robot robot = robotOpt.get();

        // Vérifier si le robot est connecté et disponible
        if (robot.getStatus() != RobotStatus.CONNECTED) {
            return ResponseEntity.badRequest().body("Erreur : Le robot sélectionné n'est pas connecté !");
        }
        if (robot.isEnMission()) {
            return ResponseEntity.badRequest().body("Erreur : Le robot est déjà en mission !");
        }

        // Récupérer les machines associées
        List<machine> machines = machineRepository.findAllById(request.getMachineIds());

        // Vérifier si toutes les machines existent
        List<Long> missingMachineIds = request.getMachineIds().stream()
                .filter(id -> machines.stream().noneMatch(m -> m.getId().equals(id)))
                .collect(Collectors.toList());

        if (!missingMachineIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Erreur : Les machines suivantes n'existent pas : " + missingMachineIds);
        }

        // Créer la mission avec le statut PENDING
        Mission mission = missionService.createMission(request.getNom(), robot, machines);
        mission.setStatus(MissionStatus.PENDING);
        missionRepository.save(mission);

        // Mettre le robot en mission
        robot.setEnMission(true);
        robotRepository.save(robot);

        return ResponseEntity.ok().body("Mission créée avec succès : " + mission);
    }

    @DeleteMapping("/{missionId}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> deleteMission(@PathVariable Long missionId) {
        Mission mission = missionRepository.findById(missionId).orElse(null);

        if (mission == null) {
            return ResponseEntity.status(404).body("Erreur : La mission avec l'ID " + missionId + " n'existe pas.");


        }
        //liberer le robot apres la mission
        Robot robot = mission.getRobot();
        robot.setEnMission(false);
        robotRepository.save(robot);
        //supprimer la mission
       missionService.deleteMission(missionId);
        return ResponseEntity.ok("Mission avec l'Id" + missionId +  " supprimée avec succès.");

    }

    @PutMapping("/{missionId}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> updateMission(@PathVariable Long missionId, @RequestBody MissionRequest request) {
        //verifier si la mission existe
        Optional<Mission> existingMissionOpt = missionRepository.findById(missionId);
        if (existingMissionOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Erreur:la mission avec l'ID" + missionId + "n'existe pas.");
        }
        Mission existingMission = existingMissionOpt.get();
        //verifier si le robot existe et est connecté
        Robot robot = robotRepository.findById(request.getRobotId()).orElse(null);
        if (robot == null) {
            return ResponseEntity.badRequest().body("Erreur:Robot non trouvé avec l'ID:" + request.getRobotId());

        }
        if (robot.getStatus() != RobotStatus.CONNECTED) {
            return ResponseEntity.badRequest().body("Erreur:le robot selectionne n'est pas connecté!");

        }
        if (robot.isEnMission() && !robot.getId().equals(existingMission.getRobot().getId())) {
            return ResponseEntity.badRequest().body("Erreur le robot est deja en mission!");
        }
        //verifier et recupere  les nouvelles machines
        List<machine> machines = machineRepository.findAllById(request.getMachineIds());
        if (machines.size() != request.getMachineIds().size()) {
            return ResponseEntity.badRequest().body("Erreur:Certaines machines n'existent pas!");

        }
        existingMission.setNom(request.getNom());
        existingMission.setRobot(robot);
        //Sauvegarder les modifications
        Mission updateMission = missionRepository.save(existingMission);
        return ResponseEntity.ok(updateMission);
    }

    //Demarrer  la mission
    @PostMapping("/{missionId}/start")
    public ResponseEntity<?> startMission(@PathVariable Long missionId) {
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if (missionOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Erreur:Mission introuvable.");

        }
        Mission mission = missionOpt.get();
        //verifier que la mission est en attente
        if (mission.getStatus() != MissionStatus.PENDING) {
            return ResponseEntity.badRequest().body("Erreur : La mission n'est pas en attente.");
        }
        // Vérifier si le robot est en panne
        if (mission.getRobot().getStatus() == RobotStatus.DISCONNECTED) {
            mission.setStatus(MissionStatus.FAILED);
            missionRepository.save(mission);
            return ResponseEntity.badRequest().body("Erreur : Le robot est en panne !");
        }

        // Démarrer la mission
        mission.setStatus(MissionStatus.IN_PROGRESS);
        missionRepository.save(mission);
        missionService.startmission(missionId);

        return ResponseEntity.ok("Mission démarrée !");
    }

    //passer a l'etape suivante(IN PROGRSS ->COMPLETED)
    @PostMapping("/{missionId}/next-step")
    public ResponseEntity<?> nextStep(@PathVariable Long missionId) {
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if (missionOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Erreur:Mission introuvable");

        }
        Mission mission = missionOpt.get();
        //verifier que la mission est bien en cours
        if (mission.getStatus() != MissionStatus.IN_PROGRESS) {
            return ResponseEntity.badRequest().body("Erreur:La mission n'est pas en cours ");
        }
       // missionService.nextStep(missionId);
//verifier si le robot a atteint  toutes  les machines
        if (missionService.isMissionCompleted(missionId)) {
            mission.setStatus(MissionStatus.COMPLETED);
            missionRepository.save(mission);
            return ResponseEntity.ok("Mission réussie !");
        }

        return ResponseEntity.ok("Prochaine étape exécutée.");
    }

    // 🔹 Mettre la mission en pause (IN_PROGRESS -> PAUSED)
    @PostMapping("/{missionId}/pause")
    public ResponseEntity<?> pauseMission(@PathVariable Long missionId) {
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if (missionOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Erreur : Mission introuvable.");
        }
        Mission mission = missionOpt.get();
        // Vérifier que la mission est en cours
        if (mission.getStatus() != MissionStatus.IN_PROGRESS) {
            return ResponseEntity.badRequest().body("Erreur : Seules les missions en cours peuvent être mises en pause.");
        }
        mission.setStatus(MissionStatus.PAUSED);
        missionRepository.save(mission);
        return ResponseEntity.ok("Mission mise en pause");
    }

    //reprendre la mission (en pause -> In Progress)
    @PostMapping("/{missionId}/resume")
    public ResponseEntity<?> resumeMission(@PathVariable Long missionId) {
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if (missionOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Erreur:Mission introuvable");
        }
        Mission mission = missionOpt.get();
        //Verifier que la mission est bien en pause
        if (mission.getStatus() != MissionStatus.PAUSED) {
            return ResponseEntity.badRequest().body("Erreur : Seules les missions en pause peuvent être reprises.");
        }
        mission.setStatus(MissionStatus.IN_PROGRESS);
        missionRepository.save(mission);
        return ResponseEntity.ok("mission reprise");


    }
    //Arreter une mission(Paused /In progressed ->failed )
    @PostMapping("/{missionId}/fail")
public ResponseEntity<?>failMission(@PathVariable Long missionId){
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if(missionOpt.isEmpty()){
            return ResponseEntity.status(404).body("Erreur : mission introuvable");
        }
        Mission mission=missionOpt.get();
        mission.setStatus(MissionStatus.FAILED);
        missionRepository.save(mission);
        return ResponseEntity.ok("Mission echoué");
    }
}



package com.rima.ryma_prj.application.service;

import com.rima.ryma_prj.domain.model.*;
import com.rima.ryma_prj.domain.repository.MachineRepository;
import com.rima.ryma_prj.domain.repository.MissionRepository;
import com.rima.ryma_prj.domain.repository.MissionStepRepository;
import com.rima.ryma_prj.domain.repository.RobotRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MissionService {

    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private MissionStepRepository missionStepRepository;

    //creer une mission avec des etapes
    public Mission createMission(String name,Robot robot,List<machine>machines){
        Mission mission= new Mission(name,robot);
        missionRepository.save(mission);
        int stepOrder =1;
        for(machine machine:machines){
            MissionStep step= new MissionStep(mission,machine,stepOrder++);
            missionStepRepository.save(step);
        }
        return mission;
    }
    //demarrer la mission
    public void startmission(Long missionId){
        Mission mission=missionRepository.findById(missionId).orElseThrow();
        mission.setStatus(MissionStatus.IN_PROGRESS);
        missionRepository.save(mission);

        //Metrre la prmiere  etape en cours
        List<MissionStep>steps=missionStepRepository.findByMissionIdOrderByStepOrder(missionId);
        if (!steps.isEmpty()){
            MissionStep firstStep=steps.get(0);
            firstStep.setStatus(MissionStepStatus.IN_PROGRESS);
            missionStepRepository.save(firstStep);
        }
    }
    // Continuer la mission à l'étape suivante
    public void nextStep(Long missionId) {
        List<MissionStep> steps = missionStepRepository.findByMissionIdOrderByStepOrder(missionId);
        for (MissionStep step : steps) {
            if (step.getStatus() == MissionStepStatus.PENDING) {
                step.setStatus(MissionStepStatus.IN_PROGRESS);
                missionStepRepository.save(step);
                break;
            }
        }
        // Vérifier si la mission est terminée
        if (steps.stream().allMatch(s -> s.getStatus() == MissionStepStatus.COMPLETED)) {
            Mission mission = missionRepository.findById(missionId).get();
            mission.setStatus(MissionStatus.COMPLETED);
            missionRepository.save(mission);
        }
    }
    public Mission updateMission(Long missionId, String nom, Robot robot, List<machine> machines) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée avec l'ID : " + missionId));

        mission.setNom(nom);
        mission.setRobot(robot);
        mission.setMachines(machines);

        return missionRepository.save(mission);
    }
    public void deleteMission(Long missionId) {
        missionRepository.deleteById(missionId);
    }


}
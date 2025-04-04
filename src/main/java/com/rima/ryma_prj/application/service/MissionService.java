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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MissionService {
    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private RobotRepository robotRepository;
    @Autowired
    private  MachineRepository machineRepository;
    //creer une mission
    public Mission createMission(String nom,Robot robot,List<machine>machines){
        Mission mission=new Mission();
        mission.setNom(nom);
        mission.setRobot(robot);
        mission.setMachines(machines);
        mission.setStatus(MissionStatus.PENDING);
        return missionRepository.save(mission);
    }


    public void startmission(Long missionId) {
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if (missionOpt.isPresent()) {
            Mission mission = missionOpt.get();
            if (mission.getStatus() == MissionStatus.PENDING) {
                mission.setStatus(MissionStatus.IN_PROGRESS);
                missionRepository.save(mission);
            }
        }
    }
    //passer a letape suivant
    public void nextStep(Long missionId){
        Optional<Mission>missionOpt=missionRepository.findById(missionId);
        if(missionOpt.isPresent()){
            Mission mission=missionOpt.get();
            // Vérifier si la mission est bien en cours
            if (mission.getStatus() == MissionStatus.IN_PROGRESS) {
                // Vérifier si la mission est terminée
                if (isMissionCompleted(missionId)) {
                    mission.setStatus(MissionStatus.COMPLETED);
                }
                missionRepository.save(mission);
            }
        }
    }
    // 🔹 Vérifier si la mission est terminée (le robot a atteint toutes les machines)
    public boolean isMissionCompleted(Long missionId) {
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if (missionOpt.isPresent()) {
            Mission mission = missionOpt.get();
            List<machine> machines = mission.getMachines();

            // Vérifier si toutes les machines ont été atteintes par le robot
            for (machine m : machines) {
                if (!m.isProcessed()) { // Supposons que 'isProcessed' indique si la machine a été atteinte
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    //mettre une mission en pause
    public void pauseMission(Long missionId){
        Optional<Mission>missionOpt=missionRepository.findById(missionId);
        if(missionOpt.isPresent()){
            Mission mission=missionOpt.get();
            if (mission.getStatus()==MissionStatus.IN_PROGRESS){
                mission.setStatus(MissionStatus.PAUSED);
                missionRepository.save(mission);

            }
        }
    }
    //reprendre une mission en pause
    public void resumeMission(Long missionId){
        Optional<Mission>missionOpt=missionRepository.findById(missionId);
        if(missionOpt.isPresent()){
            Mission mission=missionOpt.get();
            if(mission.getStatus()==MissionStatus.PAUSED){
                mission.setStatus(MissionStatus.IN_PROGRESS);
                missionRepository.save(mission);
            }
        }
    }
    //marquer une mission comme echouéé
    public void failMission(Long missionId){
        Optional<Mission>missionOpt=missionRepository.findById(missionId);
        if(missionOpt.isPresent()){
            Mission mission = missionOpt.get();
            mission.setStatus(MissionStatus.FAILED);
            missionRepository.save(mission);
        }

    }
    //supprimer mission
    public void deleteMission(Long missionId){
        missionRepository.deleteById(missionId);
    }



}
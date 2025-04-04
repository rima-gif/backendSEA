package com.rima.ryma_prj.infrastructure.DTO;

import com.rima.ryma_prj.domain.model.MissionStatus;

import java.util.List;

public class MissionRequest {
    private String nom;
    private  Long robotId;
    private List<Long>machineIds;

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    private MissionStatus status;  // Assurez-vous que c'est bien de type MissionStatus

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getRobotId() {
        return robotId;
    }

    public void setRobotId(Long robotId) {
        this.robotId = robotId;
    }

    public List<Long> getMachineIds() {
        return machineIds;
    }

    public void setMachineIds(List<Long> machineIds) {
        this.machineIds = machineIds;
    }
}

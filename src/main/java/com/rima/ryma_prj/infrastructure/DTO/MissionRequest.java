package com.rima.ryma_prj.infrastructure.DTO;

import java.util.List;

public class MissionRequest {
    private String nom;
    private  Long robotId;
    private List<Long>machineIds;
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

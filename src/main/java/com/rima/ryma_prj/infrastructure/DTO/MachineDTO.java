package com.rima.ryma_prj.infrastructure.DTO;

public class MachineDTO {
    private String name;
    private Long radioFrequencyId;  // ✅ Ajout du champ optionnel

    public MachineDTO() {}

    public MachineDTO(String name, Long radioFrequencyId) {
        this.name = name;
        this.radioFrequencyId = radioFrequencyId;
    }

    // Getters et Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRadioFrequencyId() {
        return radioFrequencyId;
    }

    public void setRadioFrequencyId(Long radioFrequencyId) {
        this.radioFrequencyId = radioFrequencyId;
    }
}
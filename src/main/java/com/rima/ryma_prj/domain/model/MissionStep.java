package com.rima.ryma_prj.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="mission_step")
@Getter
@Setter
@NoArgsConstructor
public class MissionStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="mission_id")
    private Mission mission;

    @ManyToOne
    @JoinColumn(name="machine_id")
    private machine machine;
    @Enumerated(EnumType.STRING)
    private MissionStepStatus status = MissionStepStatus.PENDING;
    private int stepOrder;

    public MissionStep(Mission mission,machine machine,int stepOrder){
        this.mission=mission;
        this.machine=machine;
        this.stepOrder=stepOrder;
    }
    public MissionStepStatus getStatus(){
        return status;
    }

    public void setStatus(MissionStepStatus status) {
        this.status = status;
    }
}

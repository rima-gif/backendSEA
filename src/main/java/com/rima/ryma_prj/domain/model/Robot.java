package com.rima.ryma_prj.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name ="robot")
@Getter
@Setter
@NoArgsConstructor
public class Robot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    @Column(unique = true , nullable = false)
    private String name ;
    @Enumerated(EnumType.STRING) //pour stocker le status sous forme de chaine dans la BD
    private RobotStatus status = RobotStatus.DISCONNECTED; // Valeur par défaut

    private boolean enMission; // true =robot occupé,False=disponible

    // Constructeur avec le nom seulement
    public Robot(String name) {
        this.name = name;
        this.status = RobotStatus.DISCONNECTED; // Par défaut si non fourni
    }
    @OneToMany(mappedBy = "robot", fetch = FetchType.LAZY)
    private List<Mission> missions = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RobotStatus getStatus() {
        return status;
    }

    public void setStatus(RobotStatus status) {
        this.status = status;
    }

    //partie enMission
    public boolean isEnMission() {
        return enMission;
    }

    public void setEnMission(boolean enMission) {
        this.enMission = enMission;
    }
}
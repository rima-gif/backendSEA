package com.rima.ryma_prj.domain.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionStatus status = MissionStatus.PENDING; //par defaut

    @ManyToOne
    @JoinColumn(name = "robot_id", nullable = false)
    private Robot robot;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MissionStep> steps;
    private List<machine> machines;


    public Mission(String nom, Robot robot) {
        this.nom = nom;
        this.robot = robot;
        this.status = MissionStatus.PENDING;
        this.machines = machines;

    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Robot getRobot() {
        return robot;
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    public List<machine> getMachines() {
        return machines;
    }

    public void setMachines(List<machine> machines) {
        this.machines = machines;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }
}

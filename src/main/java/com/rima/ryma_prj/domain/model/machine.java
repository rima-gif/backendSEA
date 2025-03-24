package com.rima.ryma_prj.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name="machine")
public class machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String name ;

    @OneToOne
    @JoinColumn(name ="radio_frequency_id", referencedColumnName = "id", unique = true, nullable = true)

    private RadioFrequency radioFrequency;
    public machine(){}
    public machine(String name , RadioFrequency radioFrequency){
        this.name = name;
        this.radioFrequency = radioFrequency;
    }
    // Getters et Setters
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

    public RadioFrequency getRadioFrequency() {
        return radioFrequency;
    }

    public void setRadioFrequency(RadioFrequency radioFrequency) {
        this.radioFrequency = radioFrequency;
    }



}

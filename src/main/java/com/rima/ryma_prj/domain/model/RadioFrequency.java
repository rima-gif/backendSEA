package com.rima.ryma_prj.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name ="radio_frequency")
public class RadioFrequency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String UID;

    public RadioFrequency() {}

    public RadioFrequency(String UID) {
        this.UID = UID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        if (!UID.matches("\\d{8}")) {
            throw new IllegalArgumentException("L'UID doit contenir exactement 8 chiffres.");
        }
        this.UID = UID;
    }
}
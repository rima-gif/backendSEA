package com.rima.ryma_prj.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MissionStatus {
    PENDING,
    IN_PROGRESS,
    PAUSED,
    COMPLETED,
    FAILED;

    @JsonCreator
    public static MissionStatus fromString(String value) {
        return MissionStatus.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toString() {
        return this.name();
    }
}

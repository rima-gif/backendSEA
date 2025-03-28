package com.rima.ryma_prj.infrastructure.DTO;

import com.rima.ryma_prj.domain.model.MissionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MissionDTO {
    private Long id;
    private String name;
    private MissionStatus status;
    private Long robotId;
    private List<Long>machineIds;
    private List<Long>visitedMachineIds;
    private int currentMachineIndex;
}

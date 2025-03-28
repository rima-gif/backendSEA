package com.rima.ryma_prj.domain.repository;

import com.rima.ryma_prj.domain.model.MissionStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionStepRepository extends JpaRepository <MissionStep,Long>{
    List<MissionStep>findByMissionIdOrderByStepOrder(Long missionId);

}

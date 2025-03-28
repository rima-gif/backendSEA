package com.rima.ryma_prj.domain.repository;

import com.rima.ryma_prj.domain.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission,Long> {
}

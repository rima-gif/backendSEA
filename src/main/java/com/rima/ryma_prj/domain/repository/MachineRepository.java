package com.rima.ryma_prj.domain.repository;

import com.rima.ryma_prj.domain.model.machine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MachineRepository extends JpaRepository<machine, Long> {
    Optional<machine> findByRadioFrequencyId(Long radioFrequencyId);

}

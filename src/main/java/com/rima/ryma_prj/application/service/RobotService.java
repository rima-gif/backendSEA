package com.rima.ryma_prj.application.service;

import com.rima.ryma_prj.domain.model.Robot;
import com.rima.ryma_prj.domain.model.RobotStatus;
import com.rima.ryma_prj.domain.repository.RobotRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;


@Service
public class RobotService {

    private final RobotRepository robotRepository;

    private final Map<String, String> response = new HashMap<>();  // Déclaration et initialisation de 'response'

    public RobotService(RobotRepository robotRepository) {
        this.robotRepository = robotRepository;
    }

    public List<Robot> getAllRobots() {
        return robotRepository.findAll();
    }

    public Optional<Robot> getRobotById(Long id) {
        return robotRepository.findById(id);
    }

    public Robot updateRobot(Long id, Robot updatedRobot) {
        return robotRepository.findById(id).map(robot -> {
            // Mise à jour du nom
            if (updatedRobot.getName() != null) {
                robot.setName(updatedRobot.getName());
            }

            // Mise à jour du statut UNIQUEMENT s'il est fourni
            if (updatedRobot.getStatus() != null) {
                robot.setStatus(updatedRobot.getStatus());
            }

            return robotRepository.save(robot);
        }).orElseThrow(() -> new RuntimeException("Robot non trouvé avec l'ID : " + id));
    }

    public Robot addRobot(Robot robot) {
        if (robot.getStatus() == null) {
            robot.setStatus(RobotStatus.DISCONNECTED);
        }

        return robotRepository.save(robot);
    }

    public void deleteRobot(Long id) {
        if (!robotRepository.existsById(id)) {
            throw new RuntimeException("Robot non trouvé avec l'ID : " + id);
        }
        robotRepository.deleteById(id);
    }
    public Robot getRobotByName(String name) {
        return (Robot) robotRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Robot non trouvé avec le nom : " + name));
    }

    public Map<String, String> getResponse() {
        return response;
    }
}

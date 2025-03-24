package com.rima.ryma_prj.infrastructure.controller;

import com.rima.ryma_prj.application.service.RobotService;
import com.rima.ryma_prj.domain.model.Robot;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/robots")
@CrossOrigin(origins = "http://localhost:4200")


public class RobotController {
    private final RobotService robotService;

    public RobotController(RobotService robotService) {
        this.robotService = robotService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Robot> addRobot(@RequestBody Robot robot) {
        try {
            Robot createdRobot = robotService.addRobot(robot);
            return new ResponseEntity<>(createdRobot, HttpStatus.CREATED);
        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Ou un autre statut approprié
        }
    }

        @DeleteMapping("/delete/{id}")
        @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN')")
        public ResponseEntity<Map<String, String>> deleteRobot(@PathVariable Long id) {
            try {
                robotService.deleteRobot(id);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Robot supprimé avec succès.");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (RuntimeException e) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", e.getMessage());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        }
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Robot> updateRobot(@PathVariable Long id, @RequestBody Robot robot) {
        Robot updatedRobot = robotService.updateRobot(id, robot);

        if (updatedRobot != null) {
            return ResponseEntity.ok(updatedRobot);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Robot>> getAllRobots() {
        List<Robot> robots = robotService.getAllRobots();
        return ResponseEntity.ok(robots); // 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<Robot>     getRobotById(@PathVariable Long id) {
        return robotService.getRobotById(id)
                .map(ResponseEntity::ok) // 200 OK si trouvé
                .orElseGet(() -> ResponseEntity.notFound().build()); // 404 Not Found si non trouvé
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Robot> getRobotByName(@PathVariable String name) {
        Robot robot = robotService.getRobotByName(name);
        return (robot != null) ? ResponseEntity.ok(robot) : ResponseEntity.notFound().build();
    }
    @MessageMapping("/send-rfid")
    @SendTo("/topic/robot-location")
    public String sendRFID(String rfid) {
        System.out.println("RFID reçu: " + rfid);
        return rfid; // Renvoie l'UID RFID au front-end
    }


}











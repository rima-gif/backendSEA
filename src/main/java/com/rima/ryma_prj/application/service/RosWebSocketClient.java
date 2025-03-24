package com.rima.ryma_prj.application.service;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class RosWebSocketClient extends WebSocketClient {

    private final SimpMessagingTemplate messagingTemplate;

    public RosWebSocketClient(SimpMessagingTemplate messagingTemplate) throws Exception {
        super(new URI("ws://localhost:9090")); // Remplacez par l'URI de votre serveur ROS WebSocket
        this.messagingTemplate = messagingTemplate;
        connect();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Connecté à ROS WebSocket");
        send("{\"op\":\"subscribe\", \"topic\":\"/robot/location\"}");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Message reçu de ROS: " + message);

        // Extraire l'UID RFID
        String rfid = extractRFID(message);

        // Envoyer l'ID RFID au frontend via WebSocket
        messagingTemplate.convertAndSend("/topic/robot-location", rfid);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Déconnecté de ROS WebSocket: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Erreur WebSocket: " + ex.getMessage());
    }

    private String extractRFID(String message) {
        try {
            return message.split("\"rfid\":\"")[1].split("\"")[0];
        } catch (Exception e) {
            System.err.println("Erreur d'extraction de l'RFID: " + e.getMessage());
            return "UNKNOWN";
        }
    }
}

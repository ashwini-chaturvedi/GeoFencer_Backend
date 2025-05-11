package admin.admin.Controller;

import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller // Marks this class as a Spring MVC controller
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}) // Allows cross-origin requests
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate; // Handles sending messages to WebSocket clients


    @MessageMapping("/send/message") // Listens for messages sent to the "/app/send/message" endpoint
    public void sendMessage(String message) {
        System.out.println("hhh"); // Logs the received message to the console
        messagingTemplate.convertAndSend("/topic/messages", message); // Sends the received message to all clients subscribed to "/topic/messages"
    }
}
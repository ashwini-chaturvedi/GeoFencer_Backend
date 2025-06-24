package admin.admin.Controller;

import admin.admin.Service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/submit")
    public ResponseEntity<?> feedbackForm(@RequestBody String contactDetail) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        JsonNode jsonNode=objectMapper.readTree(contactDetail);

        String name=jsonNode.get("name").asText();
        String email=jsonNode.get("email").asText();
        String category=jsonNode.get("category").asText();
        String subject=jsonNode.get("subject").asText("Feedback");

        String message = "Name: " + name + "\n" +
                "Email: " + email + "\n" +
                "Category: " + category + "\n" +
                "Message: " + jsonNode.get("message").asText();

        String developerEmail="ashwinichaturvedi8924@gmail.com";

        emailService.sendEmail(developerEmail,"Feedback:"+subject,message);

        return  ResponseEntity.ok().body("Mail Sent");

    }


}

package admin.admin.Controller;

import admin.admin.Entity.OTP;
import admin.admin.Service.AdminService;
import admin.admin.Service.JWT_Service;
import admin.admin.Service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/forgot-password")
public class OTPController {

    @Autowired
    private OTPService otpService;

    @Autowired
    private JWT_Service jwtService;

    @Autowired
    private AdminService adminService;

    @PostMapping("/{email}")
    public ResponseEntity<?> generateOTP(@PathVariable String email) {
        if (!adminService.exists(email)) {
            return ResponseEntity.status(404).body("Email not found.");
        }

        String otp = otpService.generateOTP();
        otpService.saveOTP(email, otp);

        // You can send the OTP via email here (optional)

        return ResponseEntity.accepted().body("OTP sent successfully.");
    }

    @PostMapping("/verify/{email}")
    public ResponseEntity<?> verifyOTP(@PathVariable String email, @RequestBody String otp) {
        boolean isValid = otpService.verifyOTP(email, otp.substring(1,7));//OTP is coming in between double inverted commas e.g. "587122"

        System.out.println("We are here---"+otp.substring(1,7));



        if (isValid) {
            String token=jwtService.generateJWToken(email);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(400).body("Invalid or expired OTP.");
        }
    }
}

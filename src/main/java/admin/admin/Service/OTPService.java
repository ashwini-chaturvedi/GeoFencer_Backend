package admin.admin.Service;

import admin.admin.Entity.OTP;
import admin.admin.Repository.OTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(OTPService.class);


    // Save and optionally send OTP to the user's email
    public OTP saveOTP(String email, String otp) {
        try {
            // Send OTP email using EmailService
            boolean emailSent = emailService.sendOtpEmail(email, otp);

            if (!emailSent) {
                logger.warn("Failed to send OTP email to: {}", email);
                // You might want to throw an exception or handle this case
            }

            // Save OTP details to the database
            OTP otpDetails = new OTP();
            otpDetails.setEmailId(email);
            otpDetails.setOTP(otp);
            otpDetails.setExpirationDate(LocalDateTime.now().plusMinutes(10)); // OTP valid for 10 minutes

            return otpRepository.save(otpDetails);

        } catch (Exception e) {
            logger.error("Error saving OTP for email: {}", email, e);
            throw new RuntimeException("Failed to save OTP", e);
        }
    }


    // Generate a 6-digit OTP
    public String generateOTP() {
        int otp = (int)(Math.random() * 900_000) + 100_000;
        return String.valueOf(otp);
    }

    // Validate the OTP
    public boolean verifyOTP(String email, String providedOTP) {

        try{
            OTP savedOTP = otpRepository.findById(email).orElseThrow();


            boolean isMatch = savedOTP.getOTP().equals(providedOTP);
            boolean isNotExpired = LocalDateTime.now().isBefore(savedOTP.getExpirationDate());

            if (isMatch && isNotExpired) {
                otpRepository.deleteById(email); // Cleanup after successful verification
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }


        return false;
    }
}

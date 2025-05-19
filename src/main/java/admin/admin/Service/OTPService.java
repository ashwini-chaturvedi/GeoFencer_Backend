package admin.admin.Service;

import admin.admin.Entity.OTP;
import admin.admin.Repository.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private EmailService emailService;

    // Save and optionally send OTP to the user's email
    public OTP saveOTP(String email, String otp) {
        // Prepare email subject and message (if needed for email service integration)
        String subject = "OTP to Reset Your Password";
        String message = String.format("Your One-Time Password (OTP) is: %s. It is valid for 10 minutes.", otp);


         emailService.sendEmail(email, subject, message);

        // Save OTP details to the database
        OTP otpDetails = new OTP();
        otpDetails.setEmailId(email);
        otpDetails.setOTP(otp);
        otpDetails.setExpirationDate(LocalDateTime.now().plusMinutes(10)); // OTP valid for 10 minutes

        return otpRepository.save(otpDetails);
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

            System.out.println(isMatch+"-"+savedOTP.getOTP()+"-"+isNotExpired);
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

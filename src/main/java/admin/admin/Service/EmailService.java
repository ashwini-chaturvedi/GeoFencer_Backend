package admin.admin.Service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;


    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    public boolean sendEmail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            mimeMessage.setHeader("X-Priority", "1");
            MimeMessageHelper mailHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mailHelper.setTo(to);
            mailHelper.setSubject(subject);
            mailHelper.setText(body, true);

            javaMailSender.send(mimeMessage);
            logger.info("Email sent successfully to: {} with subject: {}", to, subject);
            return true;
        } catch (Exception e) {
            logger.error("Failed to send email to: {} with subject: {}", to, subject, e);
            return false;
        }
    }

    public boolean sendOtpEmail(String email, String otp) {
        String subject = "OTP to Reset Your Password";
        String message = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h2 style='color:#2E86C1;'>Password Reset Request</h2>
                        <p>Your OTP is: <strong style='font-size:24px; color:#d9534f;'>%s</strong></p>
                        <p style='color:#666;'>This OTP expires in 10 minutes.</p>
                        <p style='color:#666; font-size:12px;'>If you didn't request this, please ignore this email.</p>
                    </div>
                </body>
                </html>
                """.formatted(otp);

        return this.sendEmail(email, subject, message);
    }

    public boolean sendGeofenceAlert(String email, String adminName, String deviceName,
                                     LocalDateTime time, double lat, double lng, String distance) {
        try {
            String googleMapsLink = "https://www.google.com/maps?q=" + lat + "," + lng;
            String staticMapUrl = "";

            // Only include static map if API key is configured
            if (googleMapsApiKey != null && !googleMapsApiKey.isEmpty()) {
                staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" + lat + "," + lng +
                        "&zoom=16&size=600x300&markers=color:red|" + lat + "," + lng + "&key=" + googleMapsApiKey;
            }

            String htmlBody = String.format("""
                    <html>
                    <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                        <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                            <h2 style="color: #d9534f;">⚠️ Geofence Breach Alert</h2>
                            <p>Dear %s,</p>
                            <p>
                                This is to inform you that your device <strong>%s</strong> has <strong>breached the geofence boundary</strong>.
                            </p>
                            <h3 style="color: #333;">📍 Details:</h3>
                            <table style="border-collapse: collapse; width: 100%%;">
                                <tr><td style="padding: 5px; font-weight: bold;">Time:</td><td style="padding: 5px;">%s</td></tr>
                                <tr><td style="padding: 5px; font-weight: bold;">Latitude:</td><td style="padding: 5px;">%.6f</td></tr>
                                <tr><td style="padding: 5px; font-weight: bold;">Longitude:</td><td style="padding: 5px;">%.6f</td></tr>
                                <tr><td style="padding: 5px; font-weight: bold;">Distance from Home:</td><td style="padding: 5px;">%s meters</td></tr>
                            </table>
                            %s
                            <p style="margin-top: 20px;">
                                <a href="%s" style="color: #007bff; text-decoration: none;">📍 View on Google Maps</a>
                            </p>
                            <p style="color: #666;">Please take necessary action if this movement was unauthorized.</p>
                            <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">
                            <p style="color: #666; font-size: 12px;">
                                Regards,<br/>
                                Geofence Monitoring System
                            </p>
                        </div>
                    </body>
                    </html>
                    """,
                    adminName, deviceName, time, lat, lng, distance,
                    staticMapUrl.isEmpty() ? "" : String.format(
                            "<p style=\"text-align: center; margin: 20px 0;\"><img src=\"%s\" alt=\"Device Location Map\" style=\"max-width:100%%; border:1px solid #ccc; border-radius: 5px;\" /></p>",
                            staticMapUrl
                    ),
                    googleMapsLink);

            return this.sendEmail(email, "⚠️ Geofence Breach Alert - " + deviceName, htmlBody);

        } catch (Exception e) {
            logger.error("Failed to send geofence alert email", e);
            return false;
        }
    }


}
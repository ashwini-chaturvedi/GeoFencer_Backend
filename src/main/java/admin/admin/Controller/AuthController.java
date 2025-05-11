package admin.admin.Controller;

import admin.admin.Entity.Admin;
import admin.admin.Service.AdminUserDetailService;
import admin.admin.Service.JWT_Service;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/admin")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWT_Service jwtService;

    @Autowired
    private AdminUserDetailService userDetailsService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String githubClientId;

    @Value("${oauth2.redirect.baseurl}")
    private String oauth2RedirectBaseUrl;

    /**
     * Traditional username/password login
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody Admin admin) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            admin.getEmailId(),
                            admin.getPassword()
                    )
            );

            // If authentication is successful, generate JWT token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(admin.getEmailId());
            final String token = jwtService.generateJWToken(userDetails.getUsername());

            System.out.println("User authenticated successfully: " + userDetails.getUsername());
            System.out.println("Generated JWT token: " + token);

            // Return token in the response
            return ResponseEntity.ok(token);

        } catch (BadCredentialsException e) {
            System.out.println("Authentication failed: Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            System.out.println("Authentication error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication error occurred");
        }
    }

    /**
     * Endpoint to initiate OAuth2 login with Google
     */
    @GetMapping("/oauth2/google")
    public void initiateGoogleLogin(HttpServletResponse response) throws IOException {
        String redirectUri = oauth2RedirectBaseUrl + "/oauth2/authorization/google";
        response.sendRedirect(redirectUri);
    }

    /**
     * Endpoint to initiate OAuth2 login with GitHub
     */
    @GetMapping("/oauth2/github")
    public void initiateGithubLogin(HttpServletResponse response) throws IOException {
        String redirectUri = oauth2RedirectBaseUrl + "/oauth2/authorization/github";
        response.sendRedirect(redirectUri);
    }


}

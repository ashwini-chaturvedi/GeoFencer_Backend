package admin.admin.Controller;

import admin.admin.Entity.Admin;
import admin.admin.Service.AdminService;
import admin.admin.Service.AdminUserDetailService;
import admin.admin.Service.JWT_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWT_Service jwtService;

    @Autowired
    private AdminUserDetailService userDetailsService;

    @Autowired
    private AdminService adminService;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

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

            Map<String,Object>Details=new HashMap<>();
            Details.put("token",token);
            Details.put("userData",adminService.findByEmailId(admin.getEmailId()));
            // Return token in the response
            return ResponseEntity.ok(Details);

        } catch (BadCredentialsException e) {
            System.out.println("Authentication failed: Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            System.out.println("Authentication error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication error occurred");
        }
    }



}

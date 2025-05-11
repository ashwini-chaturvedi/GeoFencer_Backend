package admin.admin.Library;

import admin.admin.Service.JWT_Service;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom success handler for OAuth2 authentication.
 * This handler is responsible for generating a JWT token after successful OAuth2 authentication
 * and sending it in the response or redirecting the user to a URL with the token.
 */
@Component
public class OAuth2Handler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JWT_Service jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        System.out.println("OAuth2 authentication success handler triggered");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Extract user information from OAuth2User
        String email = oAuth2User.getAttribute("email");
        System.out.println("Authenticated user email: " + email);

        // Generate JWT token
        String token = jwtService.generateJWToken(email);
        System.out.println("Generated JWT token: " + token);


        String redirectUrl = "https://192.168.43.162:5173/dashboard?token=" + token + "&email=" + email;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);

    }
}
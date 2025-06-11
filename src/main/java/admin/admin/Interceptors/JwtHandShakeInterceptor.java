package admin.admin.Interceptors;

import admin.admin.Service.JWT_Service;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JwtHandShakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JWT_Service jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletHttpRequest) {
            HttpServletRequest httpRequest = servletHttpRequest.getServletRequest();
            String authHeader = httpRequest.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwtTokenFromRequest = authHeader.substring(7);

                try {
                    // ✅ Use your JWT_Service to validate
                    String email = this.jwtService.extractEmailIdFromToken(jwtTokenFromRequest);

                    // (Optional) you can also validate expiration separately if you want
                    if (email != null) {
                        attributes.put("email", email); // Store email or whatever you need
                        return true; // Allow the handshake
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false; // Reject handshake if token invalid
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // No action needed
    }
}

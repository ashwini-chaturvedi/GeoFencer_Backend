package admin.admin.Configuration;

import admin.admin.Interceptors.JwtHandShakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration // Marks this class as a configuration class for Spring Boot
@EnableWebSocketMessageBroker // Enables WebSocket messaging with STOMP
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtHandShakeInterceptor jwtHandShakeInterceptor;
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // Enables a simple message broker with a "topic" prefix for broadcasting messages
        registry.setApplicationDestinationPrefixes("/app"); // Defines "app" as the prefix for client messages
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // Defines the WebSocket endpoint that clients connect to
                .addInterceptors(jwtHandShakeInterceptor)
                .setAllowedOrigins("https://192.168.129.162:5173")// Allows requests only from the specified frontend URL
                .withSockJS(); // Enables SockJS fallback for browsers that don't support WebSockets
    }
}
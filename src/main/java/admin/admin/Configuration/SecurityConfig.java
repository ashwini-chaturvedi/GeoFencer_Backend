package admin.admin.Configuration;

import admin.admin.Library.OAuth2Library;
import admin.admin.Filter.JWT_Filter;
import admin.admin.Service.AdminUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity // Enables @PreAuthorize, @PostAuthorize, @Secured annotations
public class SecurityConfig {

    @Autowired
    private AdminUserDetailService adminUserDetailService; //to get data form DB

    @Autowired
    private JWT_Filter jwtFilter;//Custom Filter

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;//it is for Spring Security Internally Provides CORS config to Spring Security

    @Autowired
    private CorsFilter corsFilter;//it is for basic browser cors filtering Registers a custom CorsFilter that configures CORS for all requests.

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                //For Websocket continuous Connection
                                "/ws/**",
                                "/topic/**",
                                "/app/**",

                                "/auth/login",//for login using username password
                                "/auth/ping",//dummy for Monitor so that the server stays alive
                                "/admin",//for Register New Admin

                                //Forgot Password
                                "/forgot-password/**",
                                "/forgot-password/verify/**",

                                "/feedback/**"//feedback

                                )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(this.authenticationProvider())
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminUserDetailService);
        provider.setPasswordEncoder(this.passwordEncoder());
        return provider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // You can adjust the strength as needed
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        // Authentication Manager will delegate to the appropriate AuthenticationProvider
        return authConfig.getAuthenticationManager();
    }
}
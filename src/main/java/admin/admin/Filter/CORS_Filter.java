package admin.admin.Filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Component
public class CORS_Filter {

    @Value("${frontend.url}")//This will Inject the FrontEnd Url from the application.properties file
    private String frontendURL;

    //Here We are making a CorsFilter Object and Annotating it with the @Bean Which means that this is now ready to be Injected where ever it is needed
    @Bean
    public CorsFilter corsFilter() {
        //it is for basic browser cors filtering Registers a custom CorsFilter that configures CORS for all requests.


        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();//used for
        final CorsConfiguration config = new CorsConfiguration();//used for Custom cors configurations

        // Allow credentials
        config.setAllowCredentials(true);//so cookies and authorization headers can be sent cross-origin

        config.setAllowedOrigins(Arrays.asList(
                frontendURL
        ));

        // Allow common HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow all headers
        config.setAllowedHeaders(Arrays.asList(
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Expose headers that frontend can access
        config.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials",
                "Authorization"
        ));

        // Apply this configuration to all paths
        source.registerCorsConfiguration("/**", config);//This applies the config to all endpoints.

        return new CorsFilter(source);
    }
}

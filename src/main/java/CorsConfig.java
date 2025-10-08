


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                            "http://localhost:3000",
                            "https://stage-decoration-app.vercel.app",
                            "https://stage-decoration-git-b84d47-joseph-antony-benedict-js-projects.vercel.app",
                            "https://stage-decoration-9tj5iuwi3-joseph-antony-benedict-js-projects.vercel.app",
                            "https://stage-decoration-2x4jp05vn-joseph-antony-benedict-js-projects.vercel.app"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization")
                        .allowCredentials(true)
                        .maxAge(3600); // cache preflight responses for 1 hour
            }
        };
    }
}

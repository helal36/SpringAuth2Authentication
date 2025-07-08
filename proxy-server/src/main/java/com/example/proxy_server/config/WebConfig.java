package com.example.proxy_server.config;


import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry r) {
        r.addMapping("/token")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("POST")
                .allowedHeaders("*");
    }
}

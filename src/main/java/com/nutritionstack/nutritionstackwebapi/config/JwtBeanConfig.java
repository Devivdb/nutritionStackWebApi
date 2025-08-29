package com.nutritionstack.nutritionstackwebapi.config;

import com.nutritionstack.nutritionstackwebapi.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtBeanConfig {
    
    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }
}

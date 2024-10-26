package com.finance.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
 public class Configuration implements WebMvcConfigurer {


     @Override
     public void addCorsMappings(CorsRegistry registry) {
          registry.addMapping("/**")
                 .allowedOrigins("http://localhost:4200")
                 .allowedMethods("GET","POST","PUT","DELETE")
                 .allowedHeaders("*");
     }

     @Bean
     public PasswordEncoder passwordEncoder(){
         return new BCryptPasswordEncoder();
     }
 }

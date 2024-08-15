package com.finance.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@org.springframework.context.annotation.Configuration
 public class Configuration implements WebMvcConfigurer {


     @Override
     public void addCorsMappings(CorsRegistry registry) {
          registry.addMapping("/**")
                 .allowedOrigins("http://localhost:4200")
                 .allowedMethods("GET","POST","PUT","DELETE")
                 .allowedHeaders("*");
     }
 }

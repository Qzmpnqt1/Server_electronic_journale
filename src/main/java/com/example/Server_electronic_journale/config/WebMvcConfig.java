package com.example.Server_electronic_journale.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Указываем абсолютный путь с префиксом "file:" – здесь пример для D:/uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:D:/uploads/");
    }
}

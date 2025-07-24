package com.travelguider.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded files - use absolute path to match UserController
        String uploadsPath = System.getProperty("user.dir") + File.separator + uploadDir + File.separator;
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadsPath);
    }
}

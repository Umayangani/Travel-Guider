package com.travelguider.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded files with proper permissions
        String uploadsPath = Paths.get(System.getProperty("user.dir"), uploadDir).toAbsolutePath().toString();
        System.out.println("Configuring static resource handler for uploads path: " + uploadsPath);
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadsPath + File.separator)
                .setCachePeriod(0); // Disable caching for development
    }
}

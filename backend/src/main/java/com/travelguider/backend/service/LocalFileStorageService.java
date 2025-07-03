package com.travelguider.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class LocalFileStorageService {
    private final String uploadDir = "uploads/";

    public String saveFile(MultipartFile file) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(dir, filename);
        file.transferTo(dest);

        return "/uploads/" + filename;
    }
}

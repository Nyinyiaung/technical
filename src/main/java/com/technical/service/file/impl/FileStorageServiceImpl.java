package com.technical.service.file.impl;

import com.technical.service.file.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.allowed-extensions}")
    private String allowedExtensionsPattern;

    @Value("${app.upload.base-url}")
    private String baseUrl;

    private Path uploadPath;

    @PostConstruct
    public void init() {
        try {
            uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            log.info("File upload directory: {}", uploadPath);
            
            // Create upload directory if it doesn't exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath);
            }
            
            // Verify directory is writable
            if (!Files.isWritable(uploadPath)) {
                throw new RuntimeException("Upload directory is not writable: " + uploadPath);
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        // Validate file type (optional)
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        
        // Check if it's an image by content type or file extension
        boolean isImage = (contentType != null && contentType.startsWith("image/")) ||
                         (originalFilename != null && isImageFile(originalFilename));
        
        if (!isImage) {
            log.info("Invalid file type: {}, filename: {}", contentType, originalFilename);
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Generate unique filename
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID() + fileExtension;

        // Store file
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("Stored file: {} at path: {}", newFilename, filePath);
        
        // Return relative path for database storage
        return uploadPath.relativize(filePath).toString();
    }

    public String getFileUrl(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        // Return full HTTP URL for frontend access
        return baseUrl + "/" + filePath.replace("\\", "/");
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    private boolean isImageFile(String filename) {
        String extension = getFileExtension(filename);
        return extension.matches(allowedExtensionsPattern);
    }

    public void deleteFile(String filePath) throws IOException {
        if (filePath != null && !filePath.isEmpty()) {
            Path path = uploadPath.resolve(filePath).normalize();
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("Deleted file: {}", path);
            }
        }
    }
}

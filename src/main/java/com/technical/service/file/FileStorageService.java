package com.technical.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    
    /**
     * Store a file and return the relative path
     * @param file the file to store
     * @return relative path of stored file
     * @throws IOException if file storage fails
     */
    String storeFile(MultipartFile file) throws IOException;
    
    /**
     * Get the full URL for accessing a file
     * @param filePath relative path of the file
     * @return full HTTP URL or null if filePath is empty
     */
    String getFileUrl(String filePath);
    
    /**
     * Delete a file by its relative path
     * @param filePath relative path of the file to delete
     * @throws IOException if file deletion fails
     */
    void deleteFile(String filePath) throws IOException;
}

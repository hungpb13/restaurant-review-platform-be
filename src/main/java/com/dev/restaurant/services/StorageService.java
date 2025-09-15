package com.dev.restaurant.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface StorageService {
    String store(MultipartFile file, String fileName);

    Optional<Resource> loadAsResource(String fileName);
}

package com.sba.post.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    Map<?, ?> deleteImage(String publicId) throws IOException;

    Map<String, String> resizeAndUpload(MultipartFile file) throws IOException;
}

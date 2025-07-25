package com.sba.post.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sba.post.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UploadImageController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam("upload") MultipartFile file) {
        try {
            Map<String, String> result = cloudinaryService.resizeAndUpload(file);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể xử lý ảnh: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/delete-image")
    public ResponseEntity<?> deleteImage(@RequestBody Map<String, String> body) {
        String publicId = body.get("public_id");
        if (publicId == null || publicId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Thiếu public_id"));
        }

        try {
            Map<?, ?> result = cloudinaryService.deleteImage(publicId);
            if ("ok".equals(result.get("result"))) {
                return ResponseEntity.ok(Map.of("message", "Xóa thành công"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy ảnh hoặc đã bị xóa"));
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể xóa ảnh: " + e.getMessage()));
        }
    }
}

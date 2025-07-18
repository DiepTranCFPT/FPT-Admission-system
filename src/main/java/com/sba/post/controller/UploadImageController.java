package com.sba.post.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UploadImageController {

    @Autowired
    private Cloudinary cloudinary;

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam("upload") MultipartFile file) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));

        String imageUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        return ResponseEntity.ok(Map.of(
                "url", imageUrl,
                "public_id", publicId
        ));
    }

    @PostMapping("/delete-image")
    public ResponseEntity<?> deleteImage(@RequestBody Map<String, String> body) {
        String publicId = body.get("public_id");

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return ResponseEntity.ok(Map.of("message", "Xóa thành công"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể xóa ảnh"));
        }
    }
}

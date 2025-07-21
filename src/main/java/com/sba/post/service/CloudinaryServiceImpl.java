package com.sba.post.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;


@Service
@AllArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public Map<?, ?> deleteImage(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
    }

    @Override
    public Map<String, String> resizeAndUpload(MultipartFile file) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(), // Dùng file gốc luôn
                ObjectUtils.asMap("resource_type", "image")
        );

        String publicId = (String) uploadResult.get("public_id");

        String resizedUrl = cloudinary.url()
                .transformation(new Transformation()
                        .quality(70)
                        .crop("scale")
                )
                .generate(publicId);

        return Map.of(
                "url", resizedUrl,
                "public_id", publicId
        );
    }

}

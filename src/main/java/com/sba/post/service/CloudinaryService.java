package com.sba.post.service;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    Map<?, ?> deleteImage(String publicId) throws IOException;
}

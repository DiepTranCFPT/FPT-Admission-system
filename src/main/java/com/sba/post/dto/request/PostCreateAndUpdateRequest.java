package com.sba.post.dto.request;

import lombok.Data;

@Data
public class PostCreateAndUpdateRequest {
    private String title;
    private String content;
    private String category;
}

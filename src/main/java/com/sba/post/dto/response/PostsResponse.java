package com.sba.post.dto.response;

import lombok.Data;

@Data
public class PostsResponse {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String publishedAt;
    private String status;
    private String createdAt;
}

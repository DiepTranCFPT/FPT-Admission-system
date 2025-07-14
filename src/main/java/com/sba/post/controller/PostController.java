package com.sba.post.controller;

import com.sba.post.dto.request.PostCreateAndUpdateRequest;
import com.sba.post.dto.response.PostsResponse;
import com.sba.post.enums.Category;
import com.sba.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.sba.post.dto.request.PostFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    @Autowired
    private final PostService postService;

    //danh cho trang tin tuc va su kien, trang trai nghiem toan cau, va cuu sinh vien hehehe
    @GetMapping("/latest")
    public ResponseEntity<List<PostsResponse>> getLatestPosts(
            @RequestParam(defaultValue = "6") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam String category
    ) {
        try {
            Category enumCategory = Category.valueOf(category);
            List<PostsResponse> responses = postService.getLatestPosts(limit, offset, enumCategory);
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostsResponse> getPostById(@PathVariable Long id) {
        PostsResponse post = postService.findById(id);
        if (post != null) {
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    //danh cho trang admin xoa bai hehehe
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.deletePostById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Delete post successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    //danh cho trang admin tao bai post chua public
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> createPost(@RequestBody PostCreateAndUpdateRequest request, @RequestHeader("userId") String userId) {
        try {
            postService.createPost(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Create post successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid category"));
        }
    }

    //danh cho trang cap nhat bai post
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody PostCreateAndUpdateRequest request) {
        try {
            postService.updatePost(id, request);
            return ResponseEntity.ok(Map.of("message", "Update post successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    //danh cho trang admin public bai post
    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> publishPost(@PathVariable Long id)
    {
        try {
            postService.publishPost(id);
            return ResponseEntity.ok(Map.of("message", "Post published successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    //danh cho trang admin hien danh sach theo filter
    @GetMapping
    public ResponseEntity<Page<PostsResponse>> getAllPosts(
            @ModelAttribute PostFilterRequest filterRequest,
            Pageable pageable
    ) {
        Page<PostsResponse> result = postService.getFilteredPosts(filterRequest, pageable);
        return ResponseEntity.ok(result);
    }

    //danh cho trang course
    @GetMapping("/{category}/{title}")
    public ResponseEntity<PostsResponse> getPostByCategoryAndTitle(
            @PathVariable String category,
            @PathVariable String title
    ) {
        try {
            Category enumCategory = Category.valueOf(category);
            PostsResponse response = postService.getPostByCategoryAndTitle(enumCategory, title);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostsResponse>> searchPostsByTitle(
            @RequestParam String title,
            Pageable pageable
    ) {
        Page<PostsResponse> result = postService.findBySearchTitle(title, pageable);
        return ResponseEntity.ok(result);
    }
}


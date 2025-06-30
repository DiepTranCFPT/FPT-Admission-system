package com.sba.post.service;

import com.sba.post.dto.request.PostCreateAndUpdateRequest;
import com.sba.post.dto.request.PostFilterRequest;
import com.sba.post.dto.response.PostsResponse;
import com.sba.post.enums.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    List<PostsResponse> getLatestPosts(int limit, int offset, Category category);

    void deletePostById(Long id);

    @Transactional
    void createPost(PostCreateAndUpdateRequest request, String id);

    @Transactional
    void updatePost(Long postId, PostCreateAndUpdateRequest request);

    @Transactional
    void publishPost(Long postId);

    Page<PostsResponse> getFilteredPosts(PostFilterRequest request, Pageable pageable);
}

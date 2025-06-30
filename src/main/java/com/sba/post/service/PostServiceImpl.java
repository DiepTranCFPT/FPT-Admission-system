package com.sba.post.service;

import com.sba.accounts.pojos.Accounts;
import com.sba.authentications.repositories.AuthenticationRepository;
import com.sba.post.Specification.PostSpecification;
import com.sba.post.dto.request.PostCreateAndUpdateRequest;
import com.sba.post.dto.request.PostFilterRequest;
import com.sba.post.dto.response.PostsResponse;
import com.sba.post.entity.Posts;
import com.sba.post.enums.Category;
import com.sba.post.enums.Status;
import com.sba.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {
    private final AuthenticationRepository authenticationRepository;
    private final PostRepository repository;

    public PostServiceImpl(PostRepository repository, AuthenticationRepository authenticationRepository) {
        this.repository = repository;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public List<PostsResponse> getLatestPosts(int limit, int offset, Category category) {
        Optional<Posts> latestPostOpt = repository
                .findTopByStatusAndCategoryAndPublishedAtIsNotNullOrderByPublishedAtDesc(Status.PUBLISHED, category);

        if (latestPostOpt.isEmpty()) {
            return new ArrayList<>();
        }

        LocalDate latestDate = latestPostOpt.get().getPublishedAt().toLocalDate();
        LocalDateTime startOfDay = latestDate.atStartOfDay();
        LocalDateTime endOfDay = latestDate.plusDays(1).atStartOfDay().minusNanos(1);

        Pageable pageable = PageRequest.of(offset / limit, limit);

        List<Posts> posts = repository.findByStatusAndCategoryAndPublishedAtBetweenOrderByPublishedAtDesc(
                Status.PUBLISHED, category, startOfDay, endOfDay, pageable
        );

        List<PostsResponse> responses = new ArrayList<>();
        for (Posts post : posts) {
            PostsResponse response = new PostsResponse();
            response.setTitle(post.getTitle());
            response.setContent(post.getContent());
            response.setCategory(post.getCategory().getLabel());
            response.setPublishedAt(post.getPublishedAt().toString());
            responses.add(response);
        }

        return responses;
    }


    @Override
    public void deletePostById(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Post not found");
        }
        repository.deleteById(id);
    }

    @Transactional
    @Override
    public void createPost(PostCreateAndUpdateRequest request, String id) {
        Posts post = new Posts();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        Category category = Category.valueOf(request.getCategory().toUpperCase());
        post.setCategory(category);

        Accounts account = authenticationRepository.findById(id).orElseThrow();
        post.setAccounts(account);

        repository.save(post);
    }

    @Transactional
    @Override
    public void updatePost(Long postId, PostCreateAndUpdateRequest request) {

        Posts post = repository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(Category.valueOf(request.getCategory().toUpperCase()));

        repository.save(post);
    }

    @Transactional
    @Override
    public void publishPost(Long postId) {
        Posts post = repository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.getStatus() == Status.PUBLISHED) {
            throw new RuntimeException("Post is already published");
        }

        post.setStatus(Status.PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());

        repository.save(post);
    }

    @Override
    public Page<PostsResponse> getFilteredPosts(PostFilterRequest request, Pageable pageable) {
        boolean isAllNull = request.getTitle() == null && request.getCategory() == null &&
                request.getStatus() == null && request.getCreatedFrom() == null &&
                request.getCreatedTo() == null && request.getPublishedFrom() == null &&
                request.getPublishedTo() == null;

        Page<Posts> postsPage = isAllNull
                ? repository.findAll(pageable)
                : repository.findAll(PostSpecification.filter(request), pageable);

        return postsPage.map(this::convertToDto);
    }


    private PostsResponse convertToDto(Posts post) {
        PostsResponse dto = new PostsResponse();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCategory(post.getCategory().name());
        dto.setPublishedAt(String.valueOf(post.getPublishedAt()));
        dto.setStatus(post.getStatus().name());
        dto.setCreatedAt(String.valueOf(post.getCreatedAt()));
        return dto;
    }
}

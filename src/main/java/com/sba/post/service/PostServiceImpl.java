package com.sba.post.service;

import com.sba.accounts.pojos.Accounts;
import com.sba.authentications.repositories.AuthenticationRepository;
import com.sba.post.specification.PostSpecification;
import com.sba.post.dto.request.PostCreateAndUpdateRequest;
import com.sba.post.dto.request.PostFilterRequest;
import com.sba.post.dto.response.PostsResponse;
import com.sba.post.pojo.Posts;
import com.sba.post.enums.Category;
import com.sba.post.enums.Status;
import com.sba.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            response.setId(post.getId());
            response.setTitle(post.getTitle());
            response.setCategory(post.getCategory().getLabel());
            response.setPublishedAt(post.getPublishedAt().toString());
            response.setImageUrl(extractFirstImage(post.getContent()));
            responses.add(response);
        }

        return responses;
    }

    @Override
    public PostsResponse findById(Long id) {
        Posts post = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        PostsResponse response = new PostsResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setCategory(post.getCategory().name());
        response.setPublishedAt(String.valueOf(post.getPublishedAt()));
        return response;
    }

    @Override
    public void deletePostById(Long id) {
        Posts post = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        List<String> imageLinks = extractImagePaths(post.getContent());
        if (!imageLinks.isEmpty()) {
            deleteImagePaths(imageLinks);
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
        System.out.println("userId: " + id);
        System.out.println("Found account: " + account.getId());
        System.out.println("Found account: " + account.getId());
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
        post.setStatus(Status.DRAFT);
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
    public PostsResponse getPostByCategoryAndTitle(Category category, String title) {
        Posts post = repository.findByCategoryAndTitleContainingIgnoreCaseAndStatus(category, title, Status.PUBLISHED)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        PostsResponse response = new PostsResponse();
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setId(post.getId());
        return response;
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

        return postsPage.map(post -> {
            PostsResponse dto = new PostsResponse();
            dto.setId(post.getId());
            dto.setTitle(post.getTitle());
            dto.setCategory(post.getCategory().name());
            dto.setPublishedAt(String.valueOf(post.getPublishedAt()));
            dto.setStatus(post.getStatus().name());
            dto.setCreatedAt(String.valueOf(post.getCreatedAt()));
            return dto;
        });
    }

    private List<String> extractImagePaths (String htmlContent){
        List<String> imagePaths = new ArrayList<>();
        Pattern pattern = Pattern.compile("<img[^>]*src=[\"'](/uploads/[^\"']+)[\"'][^>]*>");
        Matcher matcher = pattern.matcher(htmlContent);
        while (matcher.find()) {
            String imagePath = matcher.group(1);
            imagePaths.add(imagePath);
        }
        return imagePaths;
    }

    private void deleteImagePaths(List<String> imagePaths) {
        for (String url : imagePaths) {
            String filePath = "uploads" + url.replace("/uploads", "");
            try {
                Files.deleteIfExists(java.nio.file.Paths.get(filePath));
            } catch (java.io.IOException e) {
                System.err.println("Cannot delete file: " + filePath);
            }
        }
    }

    private String extractFirstImage(String htmlContent) {
        Pattern pattern = Pattern.compile("<img[^>]*src=[\"']([^\"']+)[\"'][^>]*>");
        Matcher matcher = pattern.matcher(htmlContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}

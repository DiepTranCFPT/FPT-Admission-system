package com.sba.post.repository;

import com.sba.post.pojo.Posts;
import com.sba.post.enums.Category;
import com.sba.post.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Posts, Long> , JpaSpecificationExecutor<Posts> {
    Optional<Posts> findTopByStatusAndCategoryAndPublishedAtIsNotNullOrderByPublishedAtDesc(
            Status status,
            Category category
    );

    List<Posts> findByStatusAndCategoryAndPublishedAtBetweenOrderByPublishedAtDesc(
            Status status,
            Category category,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    Optional<Posts> findByCategoryAndTitleContainingIgnoreCaseAndStatus(
            Category category,
            String title,
            Status status
    );


    Page<Posts> findByTitleContainingIgnoreCaseAndStatus(
            String content,
            Status status,
            Pageable pageable
    );

}

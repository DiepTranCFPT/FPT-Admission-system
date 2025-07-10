package com.sba.post.specification;

import com.sba.post.dto.request.PostFilterRequest;
import com.sba.post.entity.Posts;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class PostSpecification {

    public static Specification<Posts> filter(PostFilterRequest request) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (request.getTitle() != null && !request.getTitle().isEmpty()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("title")), "%" + request.getTitle().toLowerCase() + "%"));
            }

            if (request.getCategory() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("category"), request.getCategory()));
            }

            if (request.getStatus() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getCreatedFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdAt"), request.getCreatedFrom()));
            }

            if (request.getCreatedTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createdAt"), request.getCreatedTo()));
            }

            if (request.getPublishedFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("publishedAt"), request.getPublishedFrom()));
            }

            if (request.getPublishedTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("publishedAt"), request.getPublishedTo()));
            }

            return predicate;
        };
    }
}


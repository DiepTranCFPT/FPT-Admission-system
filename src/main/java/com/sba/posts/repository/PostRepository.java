package com.sba.posts.repository;

import com.sba.posts.pojos.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, String> {
}

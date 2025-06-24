package com.sba.posts.repository;

import com.sba.posts.pojos.PostImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImgRepository extends JpaRepository<PostImg, String> {
}

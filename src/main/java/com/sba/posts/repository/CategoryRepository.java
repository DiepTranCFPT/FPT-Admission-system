package com.sba.posts.repository;

import com.sba.posts.pojos.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {

}

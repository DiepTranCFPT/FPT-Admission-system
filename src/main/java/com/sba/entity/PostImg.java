package com.sba.entity;

import com.sba.utils.BaseEntity;
import jakarta.persistence.*;

@Table(name = "Post_Imgage")
@Entity
public class PostImg extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String imageUrl;

}

package com.sba.entity;

import com.sba.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Table(name = "Post_Imgage")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class PostImg extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;



}

package com.sba.entity;

import com.sba.utils.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String id;

    private String name;

}

package com.sba.entity;

import com.sba.utils.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Campus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String id;

    private String name;

    private String address;

    private String phone;

    private String email;

    @OneToMany(mappedBy = "campus")
    private List<Major> major;

}

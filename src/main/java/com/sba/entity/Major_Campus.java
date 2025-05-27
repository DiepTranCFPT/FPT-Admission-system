package com.sba.entity;

import com.sba.utils.BaseEntity;
import jakarta.persistence.*;
import jdk.jfr.Enabled;

@Entity
@Table(name = "Campus_Major")
public class Major_Campus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

}

package com.sba.entity;

import com.sba.utils.BaseEntity;
import jakarta.persistence.*;

@Table(name = "Admision_Schedule")
@Entity
public class Schedule_Admision extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String meetLink;

}

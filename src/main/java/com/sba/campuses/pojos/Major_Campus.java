package com.sba.entity;

import com.sba.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Campus_Major")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Major_Campus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToMany
    private List<Campus> campus;

    @OneToMany
    private List<Major> major;

}

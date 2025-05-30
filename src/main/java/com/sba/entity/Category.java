package com.sba.entity;

import com.sba.utils.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String id;

    private String name;

}

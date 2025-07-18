package com.sba.campuses.pojos;

import com.sba.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "campuses")
public class Campus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String id;

    private String name;

    private String address;

    private String phone;

    private String email;

    @OneToMany(mappedBy = "campus", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Major_Campus> major_campuses;
}


package com.sba.campuses.pojos;

import com.sba.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "majors")
public class Major extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String description;

    private Double duration;

    private Double fee;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn (name = "parent_majors")
    @JsonIgnore
    private Major parentMajors;

    @OneToMany(mappedBy = "major", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Major_Campus> major_campuses;
}

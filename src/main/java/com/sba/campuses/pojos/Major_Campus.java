package com.sba.campuses.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sba.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "campus_major")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Major_Campus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private Campus campus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private Major major;

}

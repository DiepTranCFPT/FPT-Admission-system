package com.sba.entity;

import com.sba.utils.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Application extends BaseEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String id;

    private String majorName;

    private  String Scholarship;

    @ManyToOne
    private Campus campus;

    @ManyToOne
    private User user;

    @ManyToOne
    private Major major;


}

package com.sba.entity;

import com.sba.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Table(name = "Admision_Schedule")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule_Admision extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String meetLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Staff")
    private User staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User")
    private User user;

}

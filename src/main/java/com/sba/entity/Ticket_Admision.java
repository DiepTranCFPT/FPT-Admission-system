package com.sba.entity;

import com.sba.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Admision_Ticket")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket_Admision extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String email;

    private String topic;

    private String content;

    private String response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Staff")
    private User staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User")
    private User user;


}

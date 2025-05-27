package com.sba.entity;

import com.sba.utils.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "Admision_Ticket")
public class Ticket_Admision extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String email;

    private String topic;

    private String content;

    private String response;


}

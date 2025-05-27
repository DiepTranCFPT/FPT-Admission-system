package com.sba.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ChatBoxSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;
}

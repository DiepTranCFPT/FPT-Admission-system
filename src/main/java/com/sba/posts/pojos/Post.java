package com.sba.posts.pojos;

import com.sba.accounts.pojos.Accounts;
import com.sba.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String id;

    private String title;

    private String content;

    private String author; // Assuming this is a reference to the User entity

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "Poster")
    private Accounts user;

    @ManyToOne(cascade = CascadeType.ALL)
    private Category category;
}

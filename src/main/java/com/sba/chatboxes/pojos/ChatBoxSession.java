package com.sba.chatboxes.pojos;

import com.sba.accounts.pojos.Accounts;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatBoxSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private Accounts user;

}

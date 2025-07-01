package com.sba.chatboxes.pojos;

import com.sba.enums.Roles;

import com.sba.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chat_box_messages")
public class ChatBoxMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Roles role;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatBoxSession chatBoxSession;

}

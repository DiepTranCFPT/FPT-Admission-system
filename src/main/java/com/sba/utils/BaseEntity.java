package com.sba.utils;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {

    private LocalDateTime timeCreated;

    private LocalDateTime timeUpdatedLast;

    //    @Column(name = "is_delete")
    @Builder.Default
    protected boolean deleted = false;


    // khi tao 1 doi tuong moi tg se duoc tu dong lu vao voi ngay gio he thong
    @PrePersist
    protected void createDateTime() {
        timeCreated = LocalDateTime.now();
    }


    @PreUpdate
    protected void updateDateTime() {
        timeUpdatedLast = LocalDateTime.now();
    }
}

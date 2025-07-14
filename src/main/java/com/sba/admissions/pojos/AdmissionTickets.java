package com.sba.admissions.pojos;

import com.sba.accounts.pojos.Accounts;
import com.sba.enums.ProcessStatus;
import com.sba.utils.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@org.springframework.data.relational.core.mapping.Table(name = "admissiontickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdmissionTickets extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id", referencedColumnName = "id")
    private Accounts staff;

    private LocalDateTime createAt;

    @Column(length = 50)
    private String topic;

    @Column(length = 200)
    private String content;

    @Column(length = 200)
    private String response;

    private ProcessStatus status;

    @NotNull
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Accounts user;
}

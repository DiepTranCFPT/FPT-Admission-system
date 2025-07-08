package com.sba.admissions.pojos;

import com.sba.accounts.pojos.Accounts;
import com.sba.enums.ProcessStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "admissiontickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdmissionTickets {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Accounts user;
}

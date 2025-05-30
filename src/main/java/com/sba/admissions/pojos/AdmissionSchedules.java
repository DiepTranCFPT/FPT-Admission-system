package com.sba.admissions.pojos;

import com.sba.accounts.pojos.Accounts;
import com.sba.enums.ProcessStatus;
import com.sba.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "admissionschedules")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdmissionSchedules extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", referencedColumnName = "id")
    private Accounts staff;

    private LocalDateTime createAt;

    private LocalDateTime admissionAt;

    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Accounts user;

    @Column(length = 50)
    private String meetLink;

}

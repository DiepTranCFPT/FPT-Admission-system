package com.sba.admissions.repository;

import com.sba.accounts.pojos.Accounts;
import com.sba.admissions.pojos.AdmissionSchedules;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<AdmissionSchedules, String> {
    List<AdmissionSchedules> findByUser(Accounts user);
}

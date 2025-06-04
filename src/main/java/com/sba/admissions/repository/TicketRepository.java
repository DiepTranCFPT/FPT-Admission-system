package com.sba.admissions.repository;

import com.sba.admissions.pojos.AdmissionTickets;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<AdmissionTickets, String> {
}

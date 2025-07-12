package com.sba.admissions.service;

import com.sba.admissions.dto.TicketRequestDTO;
import com.sba.admissions.pojos.AdmissionTickets;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    AdmissionTickets createTicket(TicketRequestDTO ticket);
    Optional<AdmissionTickets> getTicketById(String id);
    List<AdmissionTickets> getAllTickets();
    AdmissionTickets updateTicket(String id, TicketRequestDTO ticket);
    void deleteTicket(String id);

    AdmissionTickets responseToTicket(String id, String response);


}

package com.sba.admissions.service.impl;


import com.sba.accounts.pojos.Accounts;
import com.sba.admissions.dto.TicketRequestDTO;
import com.sba.admissions.pojos.AdmissionTickets;
import com.sba.authentications.repositories.AuthenticationRepository;
import com.sba.enums.ProcessStatus;
import com.sba.admissions.repository.TicketRepository;
import com.sba.admissions.service.TicketService;
import com.sba.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private AuthenticationRepository accountsRepository;

    @Autowired
    private AccountUtils accountUtils;
//user gui ticket yc ho tro
    private AdmissionTickets mapToEntity(TicketRequestDTO dto) {
        Accounts user = accountUtils.getCurrentUser();
        AdmissionTickets ticket = new AdmissionTickets();
        ticket.setStaff(dto.getStaffId() != null ? accountsRepository.findById(dto.getStaffId()).orElse(null) : null);
        ticket.setCreateAt(LocalDateTime.now());
        ticket.setTopic(dto.getTopic());
        ticket.setContent(dto.getContent());
        ticket.setResponse("Waiting for response");
        ticket.setStatus(ProcessStatus.IN_PROCESS);
        if(user !=  null){
            ticket.setUser(user);
        }

        return ticket;
    }
    @Override
    public AdmissionTickets createTicket(TicketRequestDTO ticketRequestDTO) {
        AdmissionTickets ticket = mapToEntity(ticketRequestDTO);
        return ticketRepository.save(ticket);
    }

    @Override
    public Optional<AdmissionTickets> getTicketById(String id) {
        return ticketRepository.findById(id);
    }

    @Override
    public List<AdmissionTickets> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public AdmissionTickets updateTicket(String id, TicketRequestDTO ticketRequestDTO) {
        return ticketRepository.findById(id)
                .map(existing -> {
                    AdmissionTickets updated = mapToEntity(ticketRequestDTO);
                    updated.setId(id);
                    return ticketRepository.save(updated);
                })
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }
    @Override
    public void deleteTicket(String id) {
        ticketRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public AdmissionTickets responeToTicket(String id, String response) {
        Accounts user = accountUtils.getCurrentUser();
        AdmissionTickets admissionTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        if (admissionTicket.getStatus() == ProcessStatus.IN_PROCESS) {
            admissionTicket.setResponse(response);
            admissionTicket.setStaff(user);
            admissionTicket.setStatus(ProcessStatus.COMPLETED);
        }
        else {
            throw new RuntimeException("Ticket is not in process");
        }
        return ticketRepository.save(admissionTicket);
    }
}

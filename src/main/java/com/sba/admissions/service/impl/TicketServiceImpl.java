package com.sba.admissions.service.impl;


import com.sba.admissions.dto.TicketRequestDTO;
import com.sba.admissions.pojos.AdmissionTickets;
import com.sba.authentications.repositories.AuthenticationRepository;
import com.sba.enums.ProcessStatus;
import com.sba.admissions.repository.TicketRepository;
import com.sba.admissions.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private AuthenticationRepository accountsRepository;

    private AdmissionTickets mapToEntity(TicketRequestDTO dto) {
        AdmissionTickets ticket = new AdmissionTickets();
        ticket.setStaff(dto.getStaffId() != null ? accountsRepository.findById(dto.getStaffId()).orElse(null) : null);
        ticket.setCreateAt(dto.getCreateAt());
        ticket.setTopic(dto.getTopic());
        ticket.setContent(dto.getContent());
        ticket.setResponse(dto.getResponse());
        ticket.setStatus(dto.getStatus() != null ? ProcessStatus.valueOf(dto.getStatus()) : null);
        ticket.setUser(dto.getUserId() != null ? accountsRepository.findById(dto.getUserId()).orElse(null) : null);
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
}

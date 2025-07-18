package com.sba.admissions.service.impl;


import com.sba.accounts.pojos.Accounts;
import com.sba.admissions.dto.TicketRequestDTO;
import com.sba.admissions.pojos.AdmissionTickets;
import com.sba.authentications.repositories.AuthenticationRepository;
import com.sba.authentications.services.EmailService;
import com.sba.enums.ProcessStatus;
import com.sba.admissions.repository.TicketRepository;
import com.sba.admissions.service.TicketService;
import com.sba.model.EmailDetail;
import com.sba.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AuthenticationRepository accountsRepository;

    @Autowired
    private EmailService emailService;

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
            ticket.setEmail(user.getEmail());
        }
        else {
            ticket.setEmail(dto.getEmail());
        }
        return ticket;
    }
    @Override
    public AdmissionTickets createTicket(TicketRequestDTO ticketRequestDTO) {
        try{

            AdmissionTickets ticket = mapToEntity(ticketRequestDTO);
            return ticketRepository.save(ticket);
             } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AdmissionTickets> getTicketById(String id) {
        AdmissionTickets ticket = ticketRepository.findById(id).orElseThrow(()-> new RuntimeException("Ticket not found"));
        if(ticket.isDeleted()){
            throw new RuntimeException("Ticket has been deleted ");
        }
        return ticketRepository.findById(id);
    }

    @Override
    public List<AdmissionTickets> getAllTickets() {
        return ticketRepository.findAll().stream()
                .filter(admissionTickets -> !admissionTickets.isDeleted())
                .collect(Collectors.toList());
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
        AdmissionTickets ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
        if(ticket.isDeleted()){
            throw new RuntimeException("Ticket has been deleted");
        }
        ticket.setDeleted(true);
        ticketRepository.save(ticket);
    }

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public AdmissionTickets responseToTicket(String id, String response) {
        Accounts user = accountUtils.getCurrentUser();
        AdmissionTickets admissionTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        if (admissionTicket.getStatus() == ProcessStatus.IN_PROCESS) {
            admissionTicket.setResponse(response);
            admissionTicket.setStaff(user);
            admissionTicket.setStatus(ProcessStatus.COMPLETED);
            //sent mail
            Map<String, Object> extra = new HashMap<>();
            extra.put("ticket", admissionTicket);
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setRecipient(admissionTicket.getEmail());
            emailDetail.setSubject("Response Ticket FPTU");
            emailDetail.setName(user.getUsername());
            emailDetail.setExtra(extra);

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    emailService.sendMailTemplate(emailDetail);
                }
            };

            new Thread(r).start();
        } else {
            throw new RuntimeException("Ticket is not in process");
        }
        return ticketRepository.save(admissionTicket);
    }
}

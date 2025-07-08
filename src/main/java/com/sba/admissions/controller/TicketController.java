package com.sba.admissions.controller;

import com.sba.admissions.dto.TicketRequestDTO;
import com.sba.admissions.pojos.AdmissionTickets;
import com.sba.admissions.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<AdmissionTickets> createTicket(@RequestBody TicketRequestDTO ticketRequestDTO) {
        AdmissionTickets created = ticketService.createTicket(ticketRequestDTO);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdmissionTickets> getTicketById(@PathVariable String id) {
        return ticketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AdmissionTickets>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdmissionTickets> updateTicket(@PathVariable String id, @RequestBody TicketRequestDTO ticketRequestDTO) {
        AdmissionTickets updated = ticketService.updateTicket(id, ticketRequestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable String id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/response_ticket/{id}")
    public ResponseEntity<AdmissionTickets> responseToTicket(@PathVariable String id, @RequestBody String response) {
        AdmissionTickets updated = ticketService.responeToTicket(id, response);
        return ResponseEntity.ok(updated);
    }
}


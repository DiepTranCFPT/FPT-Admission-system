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

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<AdmissionTickets> createTicket(@RequestParam String content , @RequestParam String response ,@RequestBody String email) {
        try{
            TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
            ticketRequestDTO.setContent(content);
            ticketRequestDTO.setTopic(response);
            ticketRequestDTO.setEmail(email);
            AdmissionTickets created = ticketService.createTicket(ticketRequestDTO);
            return ResponseEntity.ok(created);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
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
        try{
            AdmissionTickets updated = ticketService.updateTicket(id, ticketRequestDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable String id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok("delete is success");
    }

    @PostMapping("/response_ticket/{id}")
    public ResponseEntity<AdmissionTickets> responseToTicket(@PathVariable String id, @RequestBody String response) {
        try {
            AdmissionTickets updated = ticketService.responseToTicket(id, response);
            return ResponseEntity.ok(updated);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().build();
        }

    }
}


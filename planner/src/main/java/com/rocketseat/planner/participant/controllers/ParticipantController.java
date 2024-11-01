package com.rocketseat.planner.participant.controllers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.participant.ParticipantEntity;
import com.rocketseat.planner.participant.ParticipantRepository;
import com.rocketseat.planner.participant.dtos.ParticipantRequestPayloadDTO;
import com.rocketseat.planner.participant.services.CreateParticipantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/participant")
public class ParticipantController {

    @Autowired
    private ParticipantRepository repository;

    @Autowired
    private CreateParticipantService createParticipantService;

    @PostMapping("/register")
    public ResponseEntity<Object> createParticipant(@Valid @RequestBody ParticipantEntity participantEntity){
        try {
            var result = this.createParticipantService.createParticipant(participantEntity);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('PARTICIPANT')")
    public ResponseEntity<ParticipantEntity> confirmParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayloadDTO payload){
        Optional<ParticipantEntity> participant = this.repository.findById(id);

        if (participant.isPresent()) {
            ParticipantEntity rawParticipant = participant.get();
            rawParticipant.setIsConfirmed(true);
            rawParticipant.setName(payload.name());

            this.repository.save(rawParticipant);

            return ResponseEntity.ok(rawParticipant);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ParticipantEntity> deleteParticipant(@PathVariable UUID id) {
        Optional<ParticipantEntity> participant = this.repository.findById(id);

        if (participant.isPresent()) {
            this.repository.delete(participant.get());

            return ResponseEntity.ok(participant.get());
        }
        return ResponseEntity.notFound().build();
    }

}

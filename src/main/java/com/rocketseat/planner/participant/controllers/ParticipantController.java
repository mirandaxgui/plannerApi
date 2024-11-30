package com.rocketseat.planner.participant.controllers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.participant.ParticipantEntity;
import com.rocketseat.planner.participant.ParticipantRepository;
import com.rocketseat.planner.participant.services.CreateParticipantService;
import com.rocketseat.planner.providers.JWTParticipantProvider;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/participant")
public class ParticipantController {

    @Autowired
    private ParticipantRepository repository;

    @Autowired
    private CreateParticipantService createParticipantService;

    @Autowired
    private JWTParticipantProvider jwtParticipantProvider;

    @PostMapping("/register")
    public ResponseEntity<Object> createParticipant(@Valid @RequestBody ParticipantEntity participantEntity) {
        try {
            var result = this.createParticipantService.createParticipant(participantEntity);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('PARTICIPANT')")
    public ResponseEntity<Object> confirmParticipant(@PathVariable UUID id, @CookieValue(value = "token", defaultValue = "") String jwtCookie) {
        // Verificar se o cookie JWT está presente
        if (jwtCookie.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT token is missing.");
        }

        // Usar o JWTParticipantProvider para validar o token e pegar os dados do participante
        String emailFromJwt = jwtParticipantProvider.getEmailFromToken(jwtCookie);
        String nameFromJwt = jwtParticipantProvider.getNameFromToken(jwtCookie);

        if (emailFromJwt == null || nameFromJwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or missing information.");
        }

        Optional<ParticipantEntity> participant = this.repository.findById(id);

        if (participant.isPresent()) {
            ParticipantEntity rawParticipant = participant.get();

            // Verificar se o e-mail no JWT corresponde ao e-mail do participante
            if (emailFromJwt.equals(rawParticipant.getEmail())) {
                rawParticipant.setIsConfirmed(true);
                rawParticipant.setName(nameFromJwt); // Atualizar o nome do participante com o nome do JWT

                this.repository.save(rawParticipant);

                return ResponseEntity.ok(rawParticipant);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Participant email is incorrect");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PARTICIPANT')")
    public ResponseEntity<ParticipantEntity> deleteParticipant(@PathVariable UUID id) {
        Optional<ParticipantEntity> participant = this.repository.findById(id);

        if (participant.isPresent()) {
            this.repository.delete(participant.get());

            return ResponseEntity.ok(participant.get());
        }
        return ResponseEntity.notFound().build();
    }

}

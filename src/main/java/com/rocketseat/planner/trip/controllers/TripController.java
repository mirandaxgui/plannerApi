package com.rocketseat.planner.trip.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.participant.dtos.ParticipantCreateResponseDTO;
import com.rocketseat.planner.participant.dtos.ParticipantDataDTO;
import com.rocketseat.planner.participant.dtos.ParticipantRequestPayloadDTO;
import com.rocketseat.planner.participant.services.ApplyGuestToEventService;
import com.rocketseat.planner.trip.TripEntity;
import com.rocketseat.planner.trip.TripRepository;
import com.rocketseat.planner.trip.dtos.TripCreateResponseDTO;
import com.rocketseat.planner.trip.dtos.TripRequestPayloadDTO;
import com.rocketseat.planner.trip.services.CreateTripService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private ApplyGuestToEventService applyGuestToEvent;

    @Autowired
    private TripRepository repository;
    @Autowired
    private CreateTripService createTripService;

    //TRIPS;
    @PostMapping("/")
    @PreAuthorize("hasRole('PARTICIPANT')")
    public ResponseEntity<TripCreateResponseDTO> createTrip(@Valid @RequestBody TripRequestPayloadDTO tripRequestPayload) {
        try {
            return this.createTripService.createTrip(tripRequestPayload);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripEntity> getTripDetails(@PathVariable UUID id) {
        Optional<TripEntity> trip = this.repository.findById(id);

        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripEntity> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayloadDTO payload) {
        Optional<TripEntity> trip = this.repository.findById(id);

        if (trip.isPresent()) {
            TripEntity rawTrip = trip.get();
            rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setDestination(payload.destination());

            this.repository.save(rawTrip);

            return ResponseEntity.ok(rawTrip);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<TripEntity> confirmTrip(@PathVariable UUID id) {
        Optional<TripEntity> trip = this.repository.findById(id);

        if (trip.isPresent()) {
            TripEntity rawTrip = trip.get();
            rawTrip.setIsConfirmed(true);

            this.repository.save(rawTrip);

            List<ParticipantDataDTO> participantList = this.applyGuestToEvent.getAllParticipantsFromEvent(id);
            for (ParticipantDataDTO participant : participantList) {
                var email = participant.email();
                this.applyGuestToEvent.triggerConfirmationEmailToParticipants(email, rawTrip);
            }

            return ResponseEntity.ok(rawTrip);
        }

        return ResponseEntity.notFound().build();
    }

    // PARTICIPANT
    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponseDTO> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayloadDTO payload) {

        Optional<TripEntity> trip = this.repository.findById(id);

        if (trip.isPresent()) {
            TripEntity rawTrip = trip.get();

            ParticipantCreateResponseDTO participantResponse = this.applyGuestToEvent.registerParticipantToEvent(payload.email(), rawTrip);

            this.applyGuestToEvent.triggerConfirmationEmailToParticipant(payload.email(), participantResponse.id(), rawTrip);

            return ResponseEntity.ok(participantResponse);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantDataDTO>> getAllParticipants(@PathVariable UUID id) {
        List<ParticipantDataDTO> participantList = this.applyGuestToEvent.getAllParticipantsFromEvent(id);

        return ResponseEntity.ok(participantList);
    }
}

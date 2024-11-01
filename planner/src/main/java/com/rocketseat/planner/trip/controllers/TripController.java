package com.rocketseat.planner.trip.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.activity.ActivityEntity;
import com.rocketseat.planner.activity.ActivityService;
import com.rocketseat.planner.activity.dtos.ActivityDataDTO;
import com.rocketseat.planner.activity.dtos.ActivityRequestPayloadDTO;
import com.rocketseat.planner.activity.dtos.ActivityResponseDTO;
import com.rocketseat.planner.link.LinkEntity;
import com.rocketseat.planner.link.LinkService;
import com.rocketseat.planner.link.dtos.LinkDataDTO;
import com.rocketseat.planner.link.dtos.LinkRequestPayloadDTO;
import com.rocketseat.planner.link.dtos.LinkResponseDTO;
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
    private ActivityService activityService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private TripRepository repository;
    @Autowired
    private CreateTripService createTripService;

    //TRIPS;

    @PostMapping("/")
    @PreAuthorize("hasRole('PARTICIPANT')")
    public ResponseEntity<TripCreateResponseDTO> createTrip(@Valid @RequestBody TripRequestPayloadDTO tripRequestPayload){
        try {
            return this.createTripService.createTrip(tripRequestPayload); 
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
    }}

    @GetMapping("/{id}")
    public ResponseEntity<TripEntity> getTripDetails(@PathVariable UUID id){
        Optional<TripEntity> trip = this.repository.findById(id);

        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripEntity> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayloadDTO payload){
        Optional<TripEntity> trip = this.repository.findById(id);

        if (trip.isPresent()){
            TripEntity rawTrip = trip.get();
            rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setDestination(payload.destination());

            this.repository.save(rawTrip);

            return ResponseEntity.ok(rawTrip);
        }

        return  ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<TripEntity> confirmTrip(@PathVariable UUID id){
        Optional<TripEntity> trip = this.repository.findById(id);

        if (trip.isPresent()){
            TripEntity rawTrip = trip.get();
            rawTrip.setIsConfirmed(true);

            this.repository.save(rawTrip);
            this.applyGuestToEvent.triggerConfirmationEmailToParticipants(id);

            return ResponseEntity.ok(rawTrip);
        }

        return ResponseEntity.notFound().build();
    }

    // ACTIVITY

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponseDTO> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayloadDTO payload){

        Optional<TripEntity> trip = this.repository.findById(id);

        if (trip.isPresent()){
            TripEntity rawTrip = trip.get();

            ActivityResponseDTO activityResponse = this.activityService.registerActivity(payload, rawTrip);

            return ResponseEntity.ok(activityResponse);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{tripId}/activities/{activityId}")
    public ResponseEntity<ActivityEntity> deleteActivity(@PathVariable UUID tripId, @PathVariable UUID activityId) {
        Optional<TripEntity> trip = this.repository.findById(tripId);

        if (trip.isPresent()) {
            Optional<ActivityEntity> activity = this.activityService.findById(activityId);

            if (activity.isPresent() && activity.get().getTrip().getId().equals(tripId)) {
                this.activityService.deleteActivity(activity.get());
                return ResponseEntity.ok(activity.get());
            }
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityDataDTO>> getAllActivities(@PathVariable UUID id){
        List<ActivityDataDTO> activityDataList = this.activityService.getAllActivitiesFromId(id);

        return ResponseEntity.ok(activityDataList);
    }


    // PARTICIPANT

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponseDTO> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayloadDTO payload){

        Optional<TripEntity> trip = this.repository.findById(id);

        if (trip.isPresent()){
            TripEntity rawTrip = trip.get();

            ParticipantCreateResponseDTO participantResponse = this.applyGuestToEvent.registerParticipantToEvent(payload.email(), rawTrip);

            if(rawTrip.getIsConfirmed()) this.applyGuestToEvent.triggerConfirmationEmailToParticipant(payload.email());

            return ResponseEntity.ok(participantResponse);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantDataDTO>> getAllParticipants(@PathVariable UUID id){
        List<ParticipantDataDTO> participantList = this.applyGuestToEvent.getAllParticipantsFromEvent(id);

        return ResponseEntity.ok(participantList);
    }

    //LINKS

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponseDTO> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayloadDTO payload){
        Optional<TripEntity> trip = this.repository.findById(id);

        if (trip.isPresent()){
            TripEntity rawTrip = trip.get();

            LinkResponseDTO linkResponse = this.linkService.registerLink(payload, rawTrip);

            return ResponseEntity.ok(linkResponse);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{tripId}/links/{linkId}")
    public ResponseEntity<LinkEntity> deleteLink(@PathVariable UUID tripId, @PathVariable UUID linkId) {
        Optional<TripEntity> trip = this.repository.findById(tripId);

        if (trip.isPresent()) {
            Optional<LinkEntity> link = this.linkService.findById(linkId);

            if (link.isPresent() && link.get().getTrip().getId().equals(tripId)) {
                this.linkService.deleteLink(link.get());
                return ResponseEntity.ok(link.get());
            }
        }

        return ResponseEntity.notFound().build();
    }


    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkDataDTO>> getAllLinks(@PathVariable UUID id){
        List<LinkDataDTO> linkDataList = this.linkService.getAllLinksFromTrip(id);

        return ResponseEntity.ok(linkDataList);
    }
}
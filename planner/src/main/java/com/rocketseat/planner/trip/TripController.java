package com.rocketseat.planner.trip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.activity.Activity;
import com.rocketseat.planner.activity.ActivityData;
import com.rocketseat.planner.activity.ActivityRequestPayload;
import com.rocketseat.planner.activity.ActivityResponse;
import com.rocketseat.planner.activity.ActivityService;
import com.rocketseat.planner.link.Link;
import com.rocketseat.planner.link.LinkData;
import com.rocketseat.planner.link.LinkRequestPayload;
import com.rocketseat.planner.link.LinkResponse;
import com.rocketseat.planner.link.LinkService;
import com.rocketseat.planner.participant.ParticipantRequestPayload;
import com.rocketseat.planner.participant.dtos.ParticipantCreateResponseDTO;
import com.rocketseat.planner.participant.dtos.ParticipantDataDTO;
import com.rocketseat.planner.participant.services.ApplyGuestToEventService;

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

    //TRIPS;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload){

        Trip newTrip = new Trip(payload);

        this.repository.save(newTrip);

        this.applyGuestToEvent.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id){
        Optional<Trip> trip = this.repository.findById(id);

        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if (trip.isPresent()){
            Trip rawTrip = trip.get();
            rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setDestination(payload.destination());

            this.repository.save(rawTrip);

            return ResponseEntity.ok(rawTrip);
        }

        return  ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id){
        Optional<Trip> trip = this.repository.findById(id);

        if (trip.isPresent()){
            Trip rawTrip = trip.get();
            rawTrip.setIsConfirmed(true);

            this.repository.save(rawTrip);
            this.applyGuestToEvent.triggerConfirmationEmailToParticipants(id);

            return ResponseEntity.ok(rawTrip);
        }

        return ResponseEntity.notFound().build();
    }

    // ACTIVITY

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload){

        Optional<Trip> trip = this.repository.findById(id);

        if (trip.isPresent()){
            Trip rawTrip = trip.get();

            ActivityResponse activityResponse = this.activityService.registerActivity(payload, rawTrip);

            return ResponseEntity.ok(activityResponse);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{tripId}/activities/{activityId}")
    public ResponseEntity<Activity> deleteActivity(@PathVariable UUID tripId, @PathVariable UUID activityId) {
        Optional<Trip> trip = this.repository.findById(tripId);

        if (trip.isPresent()) {
            Optional<Activity> activity = this.activityService.findById(activityId);

            if (activity.isPresent() && activity.get().getTrip().getId().equals(tripId)) {
                this.activityService.deleteActivity(activity.get());
                return ResponseEntity.ok(activity.get());
            }
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id){
        List<ActivityData> activityDataList = this.activityService.getAllActivitiesFromId(id);

        return ResponseEntity.ok(activityDataList);
    }


    // PARTICIPANT

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponseDTO> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload){

        Optional<Trip> trip = this.repository.findById(id);

        if (trip.isPresent()){
            Trip rawTrip = trip.get();

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
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if (trip.isPresent()){
            Trip rawTrip = trip.get();

            LinkResponse linkResponse = this.linkService.registerLink(payload, rawTrip);

            return ResponseEntity.ok(linkResponse);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{tripId}/links/{linkId}")
    public ResponseEntity<Link> deleteLink(@PathVariable UUID tripId, @PathVariable UUID linkId) {
        Optional<Trip> trip = this.repository.findById(tripId);

        if (trip.isPresent()) {
            Optional<Link> link = this.linkService.findById(linkId);

            if (link.isPresent() && link.get().getTrip().getId().equals(tripId)) {
                this.linkService.deleteLink(link.get());
                return ResponseEntity.ok(link.get());
            }
        }

        return ResponseEntity.notFound().build();
    }


    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID id){
        List<LinkData> linkDataList = this.linkService.getAllLinksFromTrip(id);

        return ResponseEntity.ok(linkDataList);
    }
}
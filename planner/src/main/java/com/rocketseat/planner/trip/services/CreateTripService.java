package com.rocketseat.planner.trip.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.exceptions.UserNotFoundException;
import com.rocketseat.planner.participant.ParticipantRepository;
import com.rocketseat.planner.participant.services.ApplyGuestToEventService;
import com.rocketseat.planner.trip.TripEntity;
import com.rocketseat.planner.trip.TripRepository;
import com.rocketseat.planner.trip.dtos.TripCreateResponseDTO;
import com.rocketseat.planner.trip.dtos.TripRequestPayloadDTO;

@Service
public class CreateTripService {

    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private ApplyGuestToEventService applyGuestToEventService;
    @Autowired
    private ParticipantRepository participantRepository;

    public ResponseEntity<TripCreateResponseDTO> createTrip(TripRequestPayloadDTO payload) {
      try {
          var email = payload.owner_email();
          
          // Verifica se o participante existe
          var participant = this.participantRepository.findByEmail(email);
          if (participant.isPresent()) {
              TripEntity tripEntity = TripEntity.builder()
                  .destination(payload.destination())
                  .isConfirmed(false)
                  .ownerEmail(payload.owner_email())
                  .ownerName(payload.owner_name())
                  .startsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME))
                  .endsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME))
                  .build();
              
              this.tripRepository.save(tripEntity);
              this.applyGuestToEventService.registerParticipantsToEvent(payload.emails_to_invite(), tripEntity);
              
              return ResponseEntity.ok(new TripCreateResponseDTO(tripEntity.getId()));
            } else {
              throw new UserNotFoundException();
          }
      } catch (Exception e) {
          throw new UserNotFoundException();
      }
    }
}

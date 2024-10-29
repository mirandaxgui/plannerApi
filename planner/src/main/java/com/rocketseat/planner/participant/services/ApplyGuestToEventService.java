package com.rocketseat.planner.participant.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.participant.ParticipantEntity;
import com.rocketseat.planner.participant.ParticipantRepository;
import com.rocketseat.planner.participant.dtos.ParticipantCreateResponseDTO;
import com.rocketseat.planner.participant.dtos.ParticipantDataDTO;
import com.rocketseat.planner.trip.TripEntity;

@Service
public class ApplyGuestToEventService {

    @Autowired
    private ParticipantRepository participantRepository;

    public void registerParticipantsToEvent(List<String> participantsToInvite, TripEntity trip) {
        List<ParticipantEntity> participants = participantsToInvite.stream().map(email -> new ParticipantEntity(email, trip)).toList();

        this.participantRepository.saveAll(participants);

        int i;
        for (i = 0; i < participants.size() ; i++ ) {
            System.out.println(participants.get(i).getId());
        }
    }

    public ParticipantCreateResponseDTO registerParticipantToEvent(String email, TripEntity trip){
        ParticipantEntity newParticipant = new ParticipantEntity(email, trip);
        this.participantRepository.save(newParticipant);

        return new ParticipantCreateResponseDTO((newParticipant.getId()));
    }

    public void triggerConfirmationEmailToParticipants(UUID tripId){}

    public void triggerConfirmationEmailToParticipant(String email){};

    public List<ParticipantDataDTO> getAllParticipantsFromEvent(UUID tripId){
        return this.participantRepository.findByTripId(tripId).stream().map(participant -> new ParticipantDataDTO(participant.getId(), participant.getName(), participant.getEmail(), participant.getIsConfirmed())).toList();
    }
}
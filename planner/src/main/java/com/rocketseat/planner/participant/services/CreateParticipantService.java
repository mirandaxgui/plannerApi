package com.rocketseat.planner.participant.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.exceptions.UserFoundException;
import com.rocketseat.planner.participant.ParticipantEntity;
import com.rocketseat.planner.participant.ParticipantRepository;

@Service
public class CreateParticipantService {
  @Autowired
    private ParticipantRepository participantRepository;

    public ParticipantEntity createParticipant(ParticipantEntity participant){
        this.participantRepository.findByEmail(participant.getEmail())
        .ifPresent(user -> {
            throw new UserFoundException();
        });
        return this.participantRepository.save(participant);
    }
}

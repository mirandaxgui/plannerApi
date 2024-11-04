package com.rocketseat.planner.participant.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.exceptions.UserFoundException;
import com.rocketseat.planner.participant.ParticipantEntity;
import com.rocketseat.planner.participant.ParticipantRepository;

@Service
public class CreateParticipantService {
    
    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ParticipantEntity createParticipant(ParticipantEntity participant){
        this.participantRepository.findByEmail(participant.getEmail())
        .ifPresent(user -> {
            throw new UserFoundException();
        });
        var email = participant.getEmail().toLowerCase();
        participant.setEmail(email);
        var password = passwordEncoder.encode(participant.getPassword());
        participant.setPassword(password);
        return this.participantRepository.save(participant);
    }
}

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

    public ParticipantEntity createParticipant(ParticipantEntity participant) {
        
        var existingParticipantOpt = this.participantRepository.findByEmail(participant.getEmail());

        if (existingParticipantOpt.isPresent()) {
            var existingParticipant = existingParticipantOpt.get();

            if (existingParticipant.getPassword() != null) {
                throw new UserFoundException();
            }
            existingParticipant.setEmail(participant.getEmail().toLowerCase());
            existingParticipant.setPassword(passwordEncoder.encode(participant.getPassword()));
            existingParticipant.setIsConfirmed(false);
            existingParticipant.setName(participant.getName());

            return this.participantRepository.save(existingParticipant);
        }

        var email = participant.getEmail().toLowerCase();
        participant.setEmail(email);
        var password = passwordEncoder.encode(participant.getPassword());
        participant.setPassword(password);
        return this.participantRepository.save(participant);
    }
}

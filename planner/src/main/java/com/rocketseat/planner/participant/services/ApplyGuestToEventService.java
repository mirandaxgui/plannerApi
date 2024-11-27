package com.rocketseat.planner.participant.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.participant.ParticipantEntity;
import com.rocketseat.planner.participant.ParticipantRepository;
import com.rocketseat.planner.participant.dtos.ParticipantCreateResponseDTO;
import com.rocketseat.planner.participant.dtos.ParticipantDataDTO;
import com.rocketseat.planner.providers.EmailSender;
import com.rocketseat.planner.trip.TripEntity;

@Service
public class ApplyGuestToEventService {

    @Autowired
    private ParticipantRepository participantRepository;

    public void registerParticipantsToEvent(List<String> participantsToInvite, TripEntity trip) {
        
        for (String email : participantsToInvite) {

            Optional<ParticipantEntity> optionalParticipant = participantRepository.findByEmail(email.toLowerCase());

            if (optionalParticipant.isPresent()) {
                ParticipantEntity existingParticipant = optionalParticipant.get();
                existingParticipant.setTrip(trip);
                participantRepository.save(existingParticipant);
                triggerConfirmationEmailToParticipant(email, existingParticipant.getId(), trip);
            } else {
                ParticipantEntity newParticipant = new ParticipantEntity(email.toLowerCase(), trip);
                participantRepository.save(newParticipant);
                triggerConfirmationEmailToParticipant(email, newParticipant.getId(), trip);

            }
        }

    }

    public ParticipantCreateResponseDTO registerParticipantToEvent(String emailParticipant, TripEntity trip) {
        String email = emailParticipant.toLowerCase(); // Converte o email para minúsculas
        Optional<ParticipantEntity> optionalParticipant = this.participantRepository.findByEmail(email);

        return optionalParticipant.map(user -> {
            user.setTrip(trip); // Atualiza a viagem do participante existente
            participantRepository.save(user);
            return new ParticipantCreateResponseDTO(user.getId()); // Retorna o ID do participante existente
        }).orElseGet(() -> {
            ParticipantEntity newParticipant = new ParticipantEntity(email, trip);
            this.participantRepository.save(newParticipant); // Salva o novo participante
            return new ParticipantCreateResponseDTO(newParticipant.getId()); // Retorna o ID do novo participante
        });
    }

    public void triggerConfirmationEmailToParticipants(String email, TripEntity trip) {
        String message = String.format("Olá, a sua participação na viagem de %s para %s foi confirmada!", trip.getOwnerName(), trip.getDestination());
        EmailSender.sendEmail(email, "Planner Viagens", message);
    }

    public void triggerConfirmationEmailToParticipant(String email, UUID participantId, TripEntity trip) {
        //MUDAR O LINK CONFORME DEPLOY
        String confirmationLink = "localhost:8080/trips/" + trip.getId();
        String message = String.format("Olá, você foi convidado para participar da viagem de %s para %s. Por favor, registre-se para confirmar a sua presença no link: \n", trip.getOwnerName(), trip.getDestination()) + confirmationLink;
        EmailSender.sendEmail(email, "Planner Viagens", message);
    }

    ;

    public List<ParticipantDataDTO> getAllParticipantsFromEvent(UUID tripId) {
        return this.participantRepository.findByTripId(tripId).stream().map(participant -> new ParticipantDataDTO(participant.getId(), participant.getName(), participant.getEmail(), participant.getIsConfirmed())).toList();
    }
}

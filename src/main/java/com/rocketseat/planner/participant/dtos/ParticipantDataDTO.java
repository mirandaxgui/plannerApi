package com.rocketseat.planner.participant.dtos;

import java.util.UUID;

public record ParticipantDataDTO(UUID id, String nome, String email, Boolean isConfirmed) {
}

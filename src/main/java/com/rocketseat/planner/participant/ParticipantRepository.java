package com.rocketseat.planner.participant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, UUID> {
    Optional<ParticipantEntity> findByEmail(String email);
    List<ParticipantEntity> findByTripId(UUID tripId);
}

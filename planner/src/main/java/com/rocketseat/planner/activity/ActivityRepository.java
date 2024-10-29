package com.rocketseat.planner.activity;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<ActivityEntity, UUID> {
    List<ActivityEntity> findByTripId(UUID tripId);
}

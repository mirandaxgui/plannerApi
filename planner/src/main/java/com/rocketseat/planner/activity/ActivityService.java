package com.rocketseat.planner.activity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.activity.dtos.ActivityDataDTO;
import com.rocketseat.planner.activity.dtos.ActivityRequestPayloadDTO;
import com.rocketseat.planner.activity.dtos.ActivityResponseDTO;
import com.rocketseat.planner.trip.TripEntity;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository repository;

    public ActivityResponseDTO registerActivity(ActivityRequestPayloadDTO payload, TripEntity trip){
        ActivityEntity newActivity = new ActivityEntity(payload.title(), payload.occurs_at(), trip);

        this.repository.save(newActivity);

        return new ActivityResponseDTO(newActivity.getId());
    }

    public List<ActivityDataDTO> getAllActivitiesFromId(UUID tripId){
        return this.repository.findByTripId(tripId).stream().map(activity -> new ActivityDataDTO(activity.getId(), activity.getTitle(), activity.getOccursAt())).toList();
    }
    
    public void deleteActivity(ActivityEntity activity){
        repository.delete(activity);
    }

    public Optional<ActivityEntity> findById(UUID activityId) {
        return repository.findById(activityId);
    }
}

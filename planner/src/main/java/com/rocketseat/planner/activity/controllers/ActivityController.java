package com.rocketseat.planner.activity.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.activity.ActivityEntity;
import com.rocketseat.planner.activity.ActivityService;
import com.rocketseat.planner.activity.dtos.ActivityDataDTO;
import com.rocketseat.planner.activity.dtos.ActivityRequestPayloadDTO;
import com.rocketseat.planner.activity.dtos.ActivityResponseDTO;
import com.rocketseat.planner.trip.TripEntity;
import com.rocketseat.planner.trip.TripRepository;

@RestController
@RequestMapping("/trips")
public class ActivityController {

    @Autowired
    private TripRepository repository;
    @Autowired
    private ActivityService activityService;

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponseDTO> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayloadDTO payload) {

        Optional<TripEntity> trip = this.repository.findById(id);

        if (trip.isPresent()) {
            TripEntity rawTrip = trip.get();

            ActivityResponseDTO activityResponse = this.activityService.registerActivity(payload, rawTrip);

            return ResponseEntity.ok(activityResponse);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{tripId}/activities/{activityId}")
    public ResponseEntity<ActivityEntity> deleteActivity(@PathVariable UUID tripId, @PathVariable UUID activityId) {
        Optional<TripEntity> trip = this.repository.findById(tripId);

        if (trip.isPresent()) {
            Optional<ActivityEntity> activity = this.activityService.findById(activityId);

            if (activity.isPresent() && activity.get().getTrip().getId().equals(tripId)) {
                this.activityService.deleteActivity(activity.get());
                return ResponseEntity.ok(activity.get());
            }
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityDataDTO>> getAllActivities(@PathVariable UUID id) {
        List<ActivityDataDTO> activityDataList = this.activityService.getAllActivitiesFromId(id);

        return ResponseEntity.ok(activityDataList);
    }
}

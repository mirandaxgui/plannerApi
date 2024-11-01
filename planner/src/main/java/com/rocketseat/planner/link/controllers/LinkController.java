package com.rocketseat.planner.link.controllers;

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

import com.rocketseat.planner.link.LinkEntity;
import com.rocketseat.planner.link.LinkService;
import com.rocketseat.planner.link.dtos.LinkDataDTO;
import com.rocketseat.planner.link.dtos.LinkRequestPayloadDTO;
import com.rocketseat.planner.link.dtos.LinkResponseDTO;
import com.rocketseat.planner.trip.TripEntity;
import com.rocketseat.planner.trip.TripRepository;

@RestController
@RequestMapping("/trips")
public class LinkController {

    @Autowired
    private TripRepository repository;

    @Autowired
    private LinkService linkService;

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponseDTO> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayloadDTO payload) {
        Optional<TripEntity> trip = this.repository.findById(id);

        if (trip.isPresent()) {
            TripEntity rawTrip = trip.get();

            LinkResponseDTO linkResponse = this.linkService.registerLink(payload, rawTrip);

            return ResponseEntity.ok(linkResponse);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{tripId}/links/{linkId}")
    public ResponseEntity<LinkEntity> deleteLink(@PathVariable UUID tripId, @PathVariable UUID linkId) {
        Optional<TripEntity> trip = this.repository.findById(tripId);

        if (trip.isPresent()) {
            Optional<LinkEntity> link = this.linkService.findById(linkId);

            if (link.isPresent() && link.get().getTrip().getId().equals(tripId)) {
                this.linkService.deleteLink(link.get());
                return ResponseEntity.ok(link.get());
            }
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkDataDTO>> getAllLinks(@PathVariable UUID id) {
        List<LinkDataDTO> linkDataList = this.linkService.getAllLinksFromTrip(id);

        return ResponseEntity.ok(linkDataList);
    }
}

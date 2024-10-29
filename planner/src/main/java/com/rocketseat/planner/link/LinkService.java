package com.rocketseat.planner.link;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.link.dtos.LinkDataDTO;
import com.rocketseat.planner.link.dtos.LinkRequestPayloadDTO;
import com.rocketseat.planner.link.dtos.LinkResponseDTO;
import com.rocketseat.planner.trip.TripEntity;

@Service
public class LinkService {
    @Autowired
    private LinkRepository repository;

    public LinkResponseDTO registerLink(LinkRequestPayloadDTO payload, TripEntity trip){
        Link newLink = new Link(payload.title(), payload.url(), trip);

        repository.save(newLink);

        return new LinkResponseDTO(newLink.getId());
    }

    public List<LinkDataDTO> getAllLinksFromTrip(UUID tripId){
        return repository.findByTripId(tripId).stream().map(link -> new LinkDataDTO(link.getId(), link.getTitle(), link.getUrl())).toList();
    }

    public Optional<Link> findById(UUID linkId) {
        return repository.findById(linkId);
    }

    public void deleteLink(Link link) {
        repository.delete(link);
    }
}

package com.rocketseat.planner.link;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.trip.TripEntity;

@Service
public class LinkService {
    @Autowired
    private LinkRepository repository;

    public LinkResponse registerLink(LinkRequestPayload payload, TripEntity trip){
        Link newLink = new Link(payload.title(), payload.url(), trip);

        repository.save(newLink);

        return new LinkResponse(newLink.getId());
    }

    public List<LinkData> getAllLinksFromTrip(UUID tripId){
        return repository.findByTripId(tripId).stream().map(link -> new LinkData(link.getId(), link.getTitle(), link.getUrl())).toList();
    }

    // Novo método para buscar um Link pelo seu ID
    public Optional<Link> findById(UUID linkId) {
        return repository.findById(linkId);
    }

    // Novo método para deletar um Link
    public void deleteLink(Link link) {
        repository.delete(link);
    }
}

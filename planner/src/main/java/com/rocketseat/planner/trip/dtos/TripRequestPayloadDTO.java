package com.rocketseat.planner.trip.dtos;

import java.util.List;

public record TripRequestPayloadDTO(String destination, String starts_at, String ends_at, List<String> emails_to_invite, String owner_email, String owner_name) {
}

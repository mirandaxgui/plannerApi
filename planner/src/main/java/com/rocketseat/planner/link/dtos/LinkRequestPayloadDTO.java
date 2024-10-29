package com.rocketseat.planner.link.dtos;

import java.util.UUID;

public record LinkRequestPayloadDTO(String title, String url, UUID id) {
}

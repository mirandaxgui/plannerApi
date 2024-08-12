package com.rocketseat.planner.link;

import java.util.UUID;

public record LinkRequestPayload(String title, String url, UUID id) {
}

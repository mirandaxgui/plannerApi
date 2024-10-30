package com.rocketseat.planner.participant.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthParticipantDTO {
  private String email;
  private String password;
}

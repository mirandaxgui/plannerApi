package com.rocketseat.planner.participant.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.participant.dtos.AuthParticipantDTO;
import com.rocketseat.planner.participant.services.AuthParticipantService;

@RestController
@RequestMapping("/participants")
public class AuthParticipantController {
  
  @Autowired
  private AuthParticipantService authParticipantService;

  @PostMapping("/auth")
  public ResponseEntity<Object> authParticipant(@RequestBody AuthParticipantDTO authParticipantDTO){
    try {
      var result = authParticipantService.execute(authParticipantDTO);
      return ResponseEntity.ok().body(result);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
  }
}

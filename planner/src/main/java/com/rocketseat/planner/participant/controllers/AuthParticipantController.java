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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/participant")
public class AuthParticipantController {
  
  @Autowired
  private AuthParticipantService authParticipantService;

  @PostMapping("/auth")
  public ResponseEntity<Object> authParticipant(@RequestBody AuthParticipantDTO authParticipantDTO, HttpServletResponse response){
    try {
      var jwtToken = authParticipantService.execute(authParticipantDTO);
      
      // Configurar o token no cookie
      Cookie jwtCookie = new Cookie("token", jwtToken);
      jwtCookie.setHttpOnly(true); // Impede acesso via JavaScript
      jwtCookie.setSecure(true); // Somente para HTTPS
      jwtCookie.setPath("/"); // Define o cookie para todas as rotas
      jwtCookie.setMaxAge(60 * 60 * 8); // Define a duração do cookie, aqui 8 horas

      // Adicionar o cookie à resposta
      response.addCookie(jwtCookie);

      return ResponseEntity.ok().body("Autenticação realizada com sucesso!");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
  }
}

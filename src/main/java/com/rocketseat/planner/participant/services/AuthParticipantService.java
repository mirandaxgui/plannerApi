package com.rocketseat.planner.participant.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.rocketseat.planner.participant.ParticipantRepository;
import com.rocketseat.planner.participant.dtos.AuthParticipantDTO;





@Service
public class AuthParticipantService {
  @Autowired
  private ParticipantRepository participantRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private final String secretKey = System.getenv("TOKEN_PARTICIPANT");

  public String execute(AuthParticipantDTO authParticipantDTO) throws AuthenticationException{
    var participant = this.participantRepository.findByEmail(authParticipantDTO.getEmail().toLowerCase()).orElseThrow(() -> {
      throw new UsernameNotFoundException("Email/password incorrect");
    });

    var passwordMatches = this.passwordEncoder.matches(authParticipantDTO.getPassword(), participant.getPassword());

    if(!passwordMatches){
      throw new AuthenticationException();
    }

    participant.setIsConfirmed(true);
    this.participantRepository.save(participant);
    
    Algorithm algorithm = Algorithm.HMAC256(secretKey);
    var expiresIn = Instant.now().plus(Duration.ofHours(8));
    var token = JWT.create().withIssuer("planner")
    .withExpiresAt(expiresIn)
    .withSubject(participant.getId().toString())
    .withClaim("roles", Arrays.asList("PARTICIPANT"))
    .withClaim("email", participant.getEmail())
    .withClaim("name", participant.getName())
    .sign(algorithm);
    
    return token;
  } 
}

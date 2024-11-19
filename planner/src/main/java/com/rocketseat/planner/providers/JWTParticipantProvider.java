package com.rocketseat.planner.providers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JWTParticipantProvider {

  @Value("${security.token.secret.participant}")
  private String secretKey;

  public DecodedJWT validateToken(String token) {
    token = token.replace("Bearer ", "");

    Algorithm algorithm = Algorithm.HMAC256(secretKey);

    try {
      var tokenDecoded = JWT.require(algorithm)
          .build()
          .verify(token);
      return tokenDecoded;
    } catch (JWTVerificationException ex) {
      return null;
    }
  }
  public String getEmailFromToken(String token) {
    DecodedJWT decodedJWT = validateToken(token);
    return decodedJWT != null ? decodedJWT.getClaim("email").asString() : null;
  }
  public String getNameFromToken(String token) {
    DecodedJWT decodedJWT = validateToken(token);
    return decodedJWT != null ? decodedJWT.getClaim("name").asString() : null;
}
}
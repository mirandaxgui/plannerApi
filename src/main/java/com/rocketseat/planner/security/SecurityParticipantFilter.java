package com.rocketseat.planner.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rocketseat.planner.providers.JWTParticipantProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityParticipantFilter extends OncePerRequestFilter {

    @Autowired
    JWTParticipantProvider jwtParticipantProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String jwtToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }

        if (jwtToken != null) {
            var tokenDecoded = jwtParticipantProvider.validateToken(jwtToken);

            if (tokenDecoded != null) {

                var roles = tokenDecoded.getClaim("roles").asList(Object.class);
                var grants = roles.stream()
                        .map(
                                role -> new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase())
                        ).toList();

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(tokenDecoded.getSubject(), null,
                        grants);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}

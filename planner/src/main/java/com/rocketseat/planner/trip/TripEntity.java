package com.rocketseat.planner.trip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.rocketseat.planner.trip.dtos.TripRequestPayloadDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity(name = "trips")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TripEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message="Este campo é obrigatório!")
    private String destination;

    @NotNull(message="Este campo é obrigatório!")
    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @NotNull(message="Este campo é obrigatório!")
    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(name = "is_confirmed")
    private Boolean isConfirmed;

    @NotBlank(message="Este campo é obrigatório!")
    @Column(name = "owner_name")
    private String ownerName;

    @NotBlank(message="Este campo é obrigatório!")
    @Column(name = "owner_email")
    private String ownerEmail;

}

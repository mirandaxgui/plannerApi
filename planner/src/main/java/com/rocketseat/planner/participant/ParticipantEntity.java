package com.rocketseat.planner.participant;

import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import com.rocketseat.planner.trip.Trip;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "is_confirmed")
    private Boolean isConfirmed;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Email(message = "O campo [email] deve conter um e-mail válido")
    private String email;

    @Column(name = "password", nullable = false)
    @Length(min = 10, max = 100, message = "A senha deve conter entre (10) e (100) caracteres")
    private String password;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    public ParticipantEntity(String email, Trip trip){
        this.email = email;
        this.trip = trip;
        this.name = "";
        this.isConfirmed = false;

    }
}

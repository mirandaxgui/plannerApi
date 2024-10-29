package com.rocketseat.planner.link;

import java.util.UUID;

import com.rocketseat.planner.trip.TripEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "links")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column (name = "url", nullable = false)
    private String url;

    @Column (nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private TripEntity trip;

    LinkEntity(String title, String url, TripEntity trip){
        this.title = title;
        this.url = url;
        this.trip = trip;
    }
}

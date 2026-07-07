package com.example.demo;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Table(name = "picks")
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Pick
{
    @Column
    private String chosenWinner;

    @Column String loser;

    @Column
    private String chosenMethod;

    @Column
    private String boutId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column
    private BigInteger betAmount;

    @Column
    private Boolean processed;

    @Column
    private String eventSlug;

    @ManyToOne
    @JoinColumn(name = "parlay_id")
    private Parlay parlay;

    @Column
    private String label;

    @Column
    private boolean completed;
}

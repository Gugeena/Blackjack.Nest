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
    public String chosenWinner;

    @Column String loser;

    @Column
    public String chosenMethod;

    @Column
    public String boutId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public AppUser user;

    @Column
    public BigInteger betAmount;

    @Column
    public Boolean processed;

    @Column
    public String eventSlug;
}

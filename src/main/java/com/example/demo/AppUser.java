package com.example.demo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "appusers")
public class AppUser
{
    @Column
    private String username;

    @Column
    private String password;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Pick> picks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Parlay> parlays;

    @Column
    private BigInteger medals;

    @Column
    private double pickAccuracy;

    @Column
    private int correctPicks;

    @Column
    private int totalPicks;
}

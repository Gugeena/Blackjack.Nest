package com.example.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parlays")
@Getter
@Setter
public class Parlay
{
    private boolean processed;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public AppUser user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "parlay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pick> picks;
}

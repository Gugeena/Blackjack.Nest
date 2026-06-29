package com.example.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "eventCache")
public class EventCache
{
    @Column
    private String eventSlug;

    @Column
    private LocalDate date;

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
}

package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParlayRepository extends JpaRepository<Parlay, Long>
{
    Parlay findByPicks(Pick pick);
    List<Parlay> findAllByUser(AppUser user);
}

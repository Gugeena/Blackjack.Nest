package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParlayService
{
    @Autowired
    private ParlayRepository parlayRepository;

    Parlay getByPick(Pick pick)
    {
        return parlayRepository.findByPicks(pick);
    }

    void save(Parlay parlay)
    {
        parlayRepository.save(parlay);
    }

    List<Parlay> getAllByUser(AppUser user)
    {
        return parlayRepository.findAllByUser(user);
    }
}

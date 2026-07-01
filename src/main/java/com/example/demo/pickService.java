package com.example.demo;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.file.LinkOption;
import java.util.List;
import java.util.Optional;

@Service
public class pickService
{
    @Autowired
    private PickRepository pickRepository;

    protected Pick UpdateOrCreate(AppUser appUser, String boutId, String winner, String method, BigInteger amount, String eventSlug, String loser)
    {
        Pick pick = pickRepository.findByUserAndBoutId(appUser, boutId).map(existing ->
        {
            existing.setChosenMethod(method);
            existing.setChosenWinner(winner);
            existing.setLoser(loser);

            existing.setLabel(winner + " By " + method);
            return existing;
        }).orElseGet(() ->
        {
            Pick newPick = new Pick();
            newPick.setChosenWinner(winner);
            newPick.setChosenMethod(method);
            newPick.setBoutId(boutId);
            newPick.setUser(appUser);
            newPick.setEventSlug(eventSlug);
            newPick.setLoser(loser);

            newPick.setLabel(winner + " By " + method);
            return newPick;
        });

        pick.setBetAmount(amount);
        pick.setProcessed(false);
        return pickRepository.save(pick);
    }

    protected Optional<Pick> getPick(AppUser appUser, String boutId)
    {
        return pickRepository.findByUserAndBoutId(appUser, boutId);
    }

    protected List<Pick> getPicksFromUser(AppUser appUser)
    {
        return pickRepository.findAllByUser(appUser);
    }

    protected List<Pick> getPicksFromIds(List<Long> Ids) { return pickRepository.findAllById(Ids); }

    protected void save(Pick pick)
    {
        pickRepository.save(pick);
    }
}

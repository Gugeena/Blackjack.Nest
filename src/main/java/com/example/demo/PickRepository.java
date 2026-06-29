package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PickRepository extends JpaRepository<Pick, Long>
{
    Optional<Pick> findByUserAndBoutId(AppUser user, String boutId);
    List<Pick> findAllByUser(AppUser appUser);
}

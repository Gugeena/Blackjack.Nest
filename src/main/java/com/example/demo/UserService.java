package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService
{
    @Autowired
    private userRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(AppUser appUser)
    {
        userRepository.save(appUser);
    }

    public Optional<AppUser> authenticateUserByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }

    public AppUser createUser(String username, String password)
    {
        AppUser newUser = new AppUser();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        return newUser;
    }

    public Boolean checkPassword(String password, AppUser appUser)
    {
        return passwordEncoder.matches(password, appUser.getPassword());
    }

    public void deleteUser(AppUser appUser)
    {
        userRepository.delete(appUser);
    }

    public boolean existsByUsername(String username)
    {
        return userRepository.findByUsername(username).isPresent();
    }
}

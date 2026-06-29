package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;

@Controller
@RequestMapping
public class AuthController
{
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/register")
    protected String displayRegister()
    {
        if(checkAuth())
        {
            return "redirect:/dashboard";
        }
        return "registerPage";
    }

    @GetMapping("/home")
    protected String displayHome()
    {
        return "homePage";
    }

    @GetMapping("")
    protected String display()
    {
        if(checkAuth())
        {
            return "redirect:/dashboard";
        }

        return "redirect:/home";
    }

    @PostMapping("/register")
    protected ResponseEntity<?> registerUser(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam(required = false) String email,
                              Model model)
    {
        boolean exists = userService.existsByUsername(username);

        if(exists)
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "error", "User Already Exists"
                ));
        }

        AppUser newUser = userService.createUser(username, password);
        if(email != null && !email.isBlank())
        {
            boolean success = emailService.isValid(email);
            if(!success)
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "Email Invalid"
                        ));
            }
            newUser.setEmail(email);
        }
        newUser.setMedals(BigInteger.valueOf(271000));
        newUser.setPickAccuracy(100);
        userService.saveUser(newUser);
        return ResponseEntity.ok(Map.of("redirect", "/login"));
    }

    @GetMapping("/login")
    protected String displayLogin()
    {
        if(checkAuth())
        {
            return "redirect:/dashboard";
        }

        return "login";
    }

    @PostMapping("/login")
    protected ResponseEntity<?> login(@RequestParam String username,
                           @RequestParam String password,
                           HttpSession httpSession,
                           HttpServletRequest request,
                           HttpServletResponse response)
    {
        Optional<AppUser> userOpt = userService.authenticateUserByUsername(username);

        if(userOpt.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User Not Found"));
        }

        AppUser appUser = userOpt.get();

        if(!userService.checkPassword(password, appUser))
        {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                   .body(Map.of("error", "Wrong Credentials"));
        }

        httpSession.setAttribute("username", username);

        if(appUser.getEmail() != null && !appUser.getEmail().isBlank())
        {
            String otp = String.valueOf(new Random().nextInt(100000, 1000000));

            httpSession.setAttribute("otp", otp);

            boolean success = emailService.sendOtp(appUser.getEmail(), otp);
            if(!success)
            {
                return ResponseEntity.ok(Map.of("redirect", "error"));
            }
            return ResponseEntity.ok(Map.of("redirect", "verify"));
        }

        setContext(appUser, httpSession);

        return ResponseEntity.ok(Map.of("redirect", "dashboard"));
    }

    @GetMapping("/verify")
    protected String displayVerify(@RequestParam(required = false) String error,
                                   Model model)
    {
        model.addAttribute("error", error);
        return "verifyPage";
    }

    @PostMapping("/verify")
    protected String verify(@RequestParam String code,
                            HttpSession httpSession,
                            HttpServletRequest request,
                            HttpServletResponse response)
    {
        String savedCode = (String) httpSession.getAttribute("otp");
        String username = (String) httpSession.getAttribute("username");

        if (!code.equals(savedCode))
        {
            return "redirect:/verify?error=wrongcode";
        }

        AppUser appUser = userService.authenticateUserByUsername(username).orElseThrow();

        setContext(appUser, httpSession);

        httpSession.removeAttribute("otp");

        return "redirect:/dashboard";
    }

    @GetMapping("/profile")
    protected String displayProfile(Model model, HttpSession httpSession)
    {
        AppUser appUser = loadInCurrentUser(httpSession);

        model.addAttribute("username", appUser.getUsername());
        model.addAttribute("medals", "Medals: " + appUser.getMedals());
        String optMail = appUser.getEmail();
        String mail = optMail != null ? optMail : "N/A";
        model.addAttribute("mail", "Email: " + mail);

        int correctPicks = appUser.getCorrectPicks();
        int totalPicks = appUser.getTotalPicks();
        double pickAccuracy = 100;
        if(totalPicks != 0)  pickAccuracy = (double) correctPicks / totalPicks * 100;
        model.addAttribute("pickAccuracy", "Pick Accuracy: " + pickAccuracy + "%");
        model.addAttribute("totalPicks", "Total picks: " + totalPicks);

        return "profilePage";
    }

    @DeleteMapping("/profile")
    protected String deleteProfile(HttpSession httpSession)
    {
        AppUser appUser = loadInCurrentUser(httpSession);
        userService.deleteUser(appUser);

        SecurityContextHolder.clearContext();
        httpSession.invalidate();

        return "redirect:/logout";
    }

    boolean checkAuth()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);
    }

    AppUser loadInCurrentUser(HttpSession httpSession)
    {
        return userService.authenticateUserByUsername((String) httpSession.getAttribute("username")).orElseThrow();
    }

    void setContext(AppUser appUser, HttpSession httpSession)
    {
        UserDetails principal = User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .roles("USER")
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }
}

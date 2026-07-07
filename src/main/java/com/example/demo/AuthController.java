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
import java.util.*;
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
        return pageOrDashboard("registerPage");
    }

    @GetMapping("/home")
    protected String displayHome()
    {
        return pageOrDashboard("homePage");
    }

    @GetMapping("")
    protected String display()
    {
        return pageOrDashboard("redirect:/home");
    }

    @PostMapping("/register")
    protected ResponseEntity<?> registerUser(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam(required = false) String email,
                              Model model,
                              HttpSession httpSession)
    {
        boolean exists = userService.existsByUsername(username);

        if(exists)
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "error", "User Already Exists"
                ));
        }

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

            httpSession.setAttribute("email", email);

            String otp = String.valueOf(new Random().nextInt(100000, 1000000));
            httpSession.setAttribute("otp", otp);
            try
            {
                emailService.sendOtp(email, otp);
            }
            catch (Exception e)
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "Make Sure Email Is Valid"
                        ));
            }

            httpSession.setAttribute("username", username);
            httpSession.setAttribute("password", password);
            httpSession.setAttribute("hasSent", true);
            return ResponseEntity.ok(Map.of("redirect", "verify"));
        }

        CreateUser(username, password, null);

        return ResponseEntity.ok(Map.of("redirect", "/login"));
    }

    @GetMapping("/login")
    protected String displayLogin()
    {
        return pageOrDashboard("login");
    }

    @PostMapping("/login")
    @ResponseBody
    protected ResponseEntity<?> login(@RequestParam String username,
                           @RequestParam String password,
                           HttpSession httpSession)
    {
        Optional<AppUser> userOpt = userService.authenticateUserByUsername(username);

        if(userOpt.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
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
            httpSession.setAttribute("hasSent", true);
            return ResponseEntity.ok(Map.of("redirect", "verify"));
        }

        setContext(appUser, httpSession);

        return ResponseEntity.ok(Map.of("redirect", "dashboard"));
    }

    @GetMapping("/verify")
    protected String displayVerify(@RequestParam(required = false) String error,
                                   Model model,
                                   HttpSession httpSession)
    {
        boolean sentCode = Boolean.TRUE.equals(httpSession.getAttribute("hasSent"));
        if(!sentCode)
        {
            return "redirect:/home";
        }
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
        String password = (String) httpSession.getAttribute("password");
        String email = (String) httpSession.getAttribute("email");

        httpSession.removeAttribute("password");
        httpSession.removeAttribute("email");
        httpSession.removeAttribute("otp");
        httpSession.setAttribute("hasSent", false);

        if (!code.equals(savedCode))
        {
            return "redirect:/verify?error=wrongcode";
        }

        if(email != null)
        {
            CreateUser(username, password, email);
            return "redirect:/login";
        }
        else
        {
            System.out.println("email is not null");
            AppUser appUser = userService.authenticateUserByUsername(username).orElseThrow();

            setContext(appUser, httpSession);

            return "redirect:/dashboard";
        }
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
        double pickAccuracy = -1;
        String pickAccuracyStr = "N/A";
        String rankStr = "N/A";
        if(totalPicks != 0)
        {
            pickAccuracy = (double) correctPicks / totalPicks * 100;
            pickAccuracyStr = pickAccuracy  + "%";

            appUser.setPickAccuracy(pickAccuracy);
            rankStr = String.valueOf(userService.getRank(pickAccuracy));
        }
        model.addAttribute("pickAccuracy", "Pick Accuracy: " + pickAccuracyStr);
        model.addAttribute("totalPicks", "Total picks: " + totalPicks);
        model.addAttribute("globalRank", "Global Rank: " + rankStr  );

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

    @GetMapping("/forgotPassword")
    protected String displayForgotPasswordPage()
    {
        return "forgotPasswordPage";
    }

    @PostMapping("/forgotPassword")
    @ResponseBody
    protected ResponseEntity<?> sendCode(@RequestParam String username, HttpSession httpSession)
    {
        String otp = String.valueOf(new Random().nextInt(100000, 1000000));
        httpSession.setAttribute("otpForForgot", otp);

        Optional<AppUser> userOpt = userService.authenticateUserByUsername(username);

        if(userOpt.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User Not Found"));
        }

        AppUser appUser = userOpt.get();

        boolean success = emailService.sendOtp(appUser.getEmail(), otp);
        if(!success)
        {
            return ResponseEntity.ok(Map.of("redirect", "error"));
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/forgotPassword")
    protected ResponseEntity<?> UpdatePassword(@RequestParam String username, @RequestParam String password)
    {
        //REMINDER FOR FUTURE LASHA SHESADZLOA SHECVALA USERMA

        try
        {
            Optional<AppUser> userOpt = userService.authenticateUserByUsername(username);

            if(userOpt.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User Not Found"));
            }

            AppUser appUser = userOpt.get();

            userService.updatePassword(appUser, password);

            return ResponseEntity.ok().build();
        }
        catch (Exception e)
        {
            //ME CHAIDANI VAR
            
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT)
                    .body(Map.of("error", "Error"));
        }
    }

    @PostMapping("/checkCode")
    @ResponseBody
    protected ResponseEntity<?> verifyCode(@RequestParam String code, HttpSession httpSession, Model model)
    {
        String otp = (String) httpSession.getAttribute("otpForForgot");
        if(!otp.equals(code))
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Wrong Code"));
        }
        httpSession.removeAttribute("otpForForgot");
        return ResponseEntity.ok(Map.of("checking", true));
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

    void CreateUser(String username, String password, String email)
    {
        AppUser newUser = userService.createUser(username, password);

        newUser.setMedals(BigInteger.valueOf(271000));
        newUser.setPickAccuracy(-1);

        if(email != null)
        {
            newUser.setEmail(email);
        }
        userService.saveUser(newUser);
    }

    String pageOrDashboard(String page)
    {
        if(checkAuth())
        {
            return "redirect:/dashboard";
        }
        return page;
    }
}

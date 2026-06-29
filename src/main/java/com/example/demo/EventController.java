package com.example.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping
public class EventController
{
    private final RestClient restClient;
    private final String upComingEP = "/ufc/events/upcoming";
    private final String slugEvent = "/ufc/events/";

    @Autowired
    private UserService userService;

    @Autowired
    private pickService pickService;

    @Autowired
    private EventCacheService eventCacheService;

    public EventController(RestClient restClient)
    {
        this.restClient = restClient;
    }

    @GetMapping("/dashboard")
    protected String displayDashboard(Model model)
    {
        EventSummaryRequest eventSummaryResponse = restClient.get()
                .uri(upComingEP)
                .header("x-api-key", "cito_36082cadcd41bc5a3eb5dbc73a5ad0f224522af9fe36d6904a2d66d848e7a716")
                .retrieve()
                .body(EventSummaryRequest.class);

        List<EventSummary> eventsSummaries = eventSummaryResponse.data;

        List<EventResponse> responseList = new ArrayList<>();

        for(EventSummary eventSummary : eventsSummaries)
        {
            EventResponse eventResponse = new EventResponse();

            String eventSlug = eventSummary.getSlug();

            eventResponse.setSlug(eventSlug);
            eventResponse.setTitle(eventSummary.getTitle());
            eventResponse.setLocationText(eventSummary.getLocationText());
            eventResponse.setStartTime(eventSummary.getStartsAt().substring(0, 10));
            eventResponse.setImage(eventSummary.getImageUrl());

            responseList.add(eventResponse);

            if(eventCacheService.checkBySlug(eventSlug)) continue;

            EventCache eventCache = new EventCache();
            eventCache.setEventSlug(eventSlug);

            String date = eventSummary.getEventDate();
            LocalDate eventLocalDate = LocalDate.parse(date);
            eventCache.setDate(eventLocalDate);

            eventCacheService.save(eventCache);
        }

        model.addAttribute("events", responseList);
    
        return "dashboardPage";
    }

    @GetMapping("/bouts/{boutSlug}")
    protected String displayBout(@PathVariable String boutSlug, Model model, HttpSession httpSession)
    {
        BoutsRequest boutsRequest =  restClient.get()
                .uri(slugEvent + "/" + boutSlug + "/bouts")
                .header("x-api-key", "cito_36082cadcd41bc5a3eb5dbc73a5ad0f224522af9fe36d6904a2d66d848e7a716")
                .retrieve()
                .body(BoutsRequest.class);

        List<Bout> bouts = boutsRequest.data;
        List<BoutResponse> response = new ArrayList<>();

        AppUser appUser = getOptAppUser(httpSession).orElse(new AppUser());

        if(appUser.getId() == null) return "redirect:/error";

        for(Bout bout : bouts)
        {
            Optional<Pick> pick = pickService.getPick(appUser, bout.getId());
            Pick Currpick = pick.orElse(null);

            Fighter firstFighter = bout.getFighters().getFirst();
            Fighter secondFighter = bout.getFighters().getLast();

            BoutResponse boutResponse = new BoutResponse();

            boutResponse.setDivision(bout.getWeightClass());

            String firstRecord = firstFighter.getProfile().getRecordText();
            String responseFirstRecord = firstRecord != null ? firstRecord.split(" ")[0] : "N/A";

            String secondRecord = secondFighter.getProfile().getRecordText();
            String responseSecondRecord = secondRecord != null ? secondRecord.split(" ")[0] : "N/A";

            String firstName = firstFighter.getFighterName();
            String[] parts = firstName.trim().split("\\s+");
            String responseFirstLastName = parts[parts.length - 1];

            String secondName = secondFighter.getFighterName();
            parts = secondName.trim().split("\\s+");
            String responseSecondLastName = parts[parts.length - 1];

            if(responseFirstLastName.equals("Jr.")) responseFirstLastName = parts[parts.length - 2];
            if(responseSecondLastName.equals("Jr.")) responseSecondLastName = parts[parts.length - 2];


            boutResponse.setFirstLastName(responseFirstLastName);
            boutResponse.setSecondLastName(responseSecondLastName);

            boutResponse.setFightersLabel( responseSecondLastName + " VS " + responseFirstLastName);
            boutResponse.setFirstFighterRecord(responseFirstRecord);
            boutResponse.setSecondFighterRecord(responseSecondRecord);
            boutResponse.setFirstFighterImage(firstFighter.getImageUrl());
            boutResponse.setSecondFighterImage(secondFighter.getImageUrl());

            boutResponse.setBoutID(bout.getId());

            boutResponse.setEventSlug(boutSlug);

            if(Currpick != null)
            {
                String currentPick = "Current Pick: " + Currpick.getChosenWinner() + " By " + Currpick.getChosenMethod();
                boutResponse.setCurrentPick(currentPick);
            }

            response.add(boutResponse);
        }

        model.addAttribute("response", response);

        return "boutPage";
    }


    @PostMapping("/pick")
    @ResponseBody
    protected ResponseEntity<?> addPick(@RequestParam String winner,
                                     @RequestParam String method,
                                     @RequestParam String boutId,
                                     @RequestParam String amount,
                                     @RequestParam String eventSlug,
                                     @RequestParam String loser,
                                     HttpSession httpSession)
    {
        AppUser appUser = getOptAppUser(httpSession).orElse(new AppUser());

        if(appUser.getId() == null)
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "error", "error occured"
                ));
        }

        BigInteger amountBet = new BigInteger(amount);

        if(amountBet.compareTo(appUser.getMedals()) > 0)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "insufficient funds"
                    ));
        }

        appUser.setTotalPicks(appUser.getTotalPicks()+1);
        pickService.UpdateOrCreate(appUser, boutId, winner, method, amountBet, eventSlug, loser);

        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @GetMapping("/mypicks")
    protected String displayMyPicks(Model model, HttpSession httpSession)
    {
        String betOutcome = "Pending";

        AppUser appUser = getOptAppUser(httpSession).orElse(new AppUser());
        if(appUser.getId() == null) return "redirect:/error";

        List<Pick> myPicks = pickService.getPicksFromUser(appUser);
        List<MyPickResponse> response = new ArrayList<>();
        HashMap<String, BoutsRequest> alreadyRequested = new HashMap<>();

        for(Pick pick : myPicks)
        {
            MyPickResponse myPickResponse = new MyPickResponse();

            String eventSlug = pick.getEventSlug();

            EventCache eventCache = eventCacheService.findBySlug(eventSlug);

            if(eventCache.getId() == null) return "redirect:/error";

            LocalDate today = LocalDate.now();
            boolean hasPassed = eventCache.getDate().isBefore(today);

            if(hasPassed)
            {
                String picksEventSlug = pick.getEventSlug();

                BoutsRequest boutsRequest;

                if(!alreadyRequested.containsKey(picksEventSlug))
                {
                   boutsRequest =  restClient.get()
                            .uri(slugEvent + "/" + picksEventSlug + "/bouts")
                            .header("x-api-key", "cito_36082cadcd41bc5a3eb5dbc73a5ad0f224522af9fe36d6904a2d66d848e7a716")
                            .retrieve()
                            .body(BoutsRequest.class);

                   if(boutsRequest == null || !boutsRequest.success || boutsRequest.data == null) continue;

                   alreadyRequested.put(picksEventSlug, boutsRequest);
                }
                else boutsRequest = alreadyRequested.get(picksEventSlug);

                List<Bout> bouts = boutsRequest.data;

                bouts = bouts.stream().filter(onBout -> onBout.getId().equals(pick.getBoutId())).toList();

                Bout bout = bouts.getFirst();

                if(!bout.getStatus().equals("completed"))
                {
                    buildPick(myPickResponse, pick, betOutcome, response);
                    continue;
                }

                String boutWinnerSlug = bout.getWinnerFighterSlug().toLowerCase();
                String boutMethod = bout.getMethod().toLowerCase();
                String chosenWinner = pick.getChosenWinner().toLowerCase();
                String chosenMethod = pick.getChosenMethod().toLowerCase();

                chosenMethod = switch(chosenMethod)
                {
                    case "submission" -> "sub";
                    case "decision" -> "dec";
                    case "tko" -> "ko/tko";
                    default -> chosenMethod;
                };

                boolean landed = true;
                if(boutWinnerSlug.contains(chosenWinner) && boutMethod.contains(chosenMethod)) betOutcome = "Correct!";
                else
                {
                    landed = false;

                    betOutcome = "The House Always Wins!";
                }

                if(!pick.getProcessed()) updateMedals(landed, appUser, pick.getBetAmount());

                pick.setProcessed(true);
            }

            buildPick(myPickResponse, pick, betOutcome, response);
        }

        model.addAttribute("picks", response);
        return "myPicksPage";
    }

    /*
    AppUser getAppUserFromOpt(HttpSession httpSession)
    {
        String username = (String) httpSession.getAttribute("username");
        Optional<AppUser> appUserOptional = userService.authenticateUserByUsername(username);
        return appUserOptional.orElse(new AppUser());
    }
     */

    void updateMedals(boolean landed, AppUser appUser, BigInteger betAmount)
    {
        BigInteger currentMedals = appUser.getMedals();

        if(landed)
        {
            BigInteger multiplier = new BigInteger("2");

            currentMedals = betAmount.multiply(multiplier).add(currentMedals);

            appUser.setCorrectPicks(appUser.getCorrectPicks()+1);
        }
        else
        {
            currentMedals = currentMedals.subtract(betAmount);
        }

        appUser.setMedals(currentMedals);
        System.out.println(appUser.getMedals());
        userService.saveUser(appUser);
    }

    Optional<AppUser> getOptAppUser(HttpSession httpSession)
    {
        String username = (String) httpSession.getAttribute("username");
        return userService.authenticateUserByUsername(username);
    }

    MyPickResponse buildPick(MyPickResponse myPickResponse, Pick pick, String betOutcome, List<MyPickResponse> response)
    {
        myPickResponse.setLabel(pick.getChosenWinner() + " VS " + pick.getLoser());
        myPickResponse.setChosenWinner("Your Pick: " + pick.getChosenWinner() + " By " + pick.getChosenMethod());
        myPickResponse.setBetAmountLabel("Bet Amount: " + pick.getBetAmount());
        myPickResponse.setStatus("Status: " + betOutcome);
        myPickResponse.setPayOutLabel("Payout: " + pick.getBetAmount().multiply(new BigInteger("2")));
        response.add(myPickResponse);

        return myPickResponse;
    }
}

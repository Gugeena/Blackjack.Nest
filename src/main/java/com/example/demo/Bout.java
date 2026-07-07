package com.example.demo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Bout
{
    public String id;
    public String eventSlug;
    public String cardSection;
    public String cardSectionOrder;
    public String cardPosition;
    public String boutOrder;
    public String weightClass;
    public String titleBout;
    public String status;
    public String isCancelled;
    public String cancellationReason;
    public String method;
    public String winnerFighterSlug;

    public List<Fighter> fighters;
}

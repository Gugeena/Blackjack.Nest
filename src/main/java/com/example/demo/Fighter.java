package com.example.demo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fighter
{
    public String fighterName;
    public String slug;
    public String fighterSlug;
    public String nickname;
    public String division;
    public String recordText;
    public String country;
    public String flag;
    public String headshotUrl;
    public String imageUrl;
    public String status;
    public String isActive;
    public FighterProfile profile;
    public String outcome;
}

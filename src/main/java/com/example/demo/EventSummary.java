package com.example.demo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EventSummary
{
    public String id;
    public String title;
    public String imageUrl;
    public String slug;
    public String locationText;
    public String startsAt;
    public String eventDate;
}

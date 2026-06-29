package com.example.demo;

import lombok.ToString;

import java.util.List;

@ToString
public class EventSummaryRequest
{
    public boolean success;
    public List<EventSummary> data;
}

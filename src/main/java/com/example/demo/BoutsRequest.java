package com.example.demo;

import lombok.ToString;

import java.util.List;

@ToString
public class BoutsRequest
{
    public boolean success;
    public List<Bout> data;
}

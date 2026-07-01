package com.example.demo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class ParlayResponse
{
    private String label;
    private List<String> pickLabels;
    private BigInteger betAmount;
    private String status;
}

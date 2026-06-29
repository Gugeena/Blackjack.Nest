package com.example.demo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class MyPickResponse
{
    private String chosenWinner;
    private String label;
    private String status;
    private String betAmountLabel;
    private String payOutLabel;
}

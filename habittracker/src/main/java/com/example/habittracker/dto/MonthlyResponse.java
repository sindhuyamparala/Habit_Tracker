package com.example.habittracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyResponse {

    private String date;
    private boolean completed;
}
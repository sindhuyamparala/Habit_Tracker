package com.example.habittracker.dto;

import java.time.LocalDate;

public class MonthlyStatusDTO {

    private LocalDate date;
    private String status;

    public MonthlyStatusDTO(LocalDate date, String status) {
        this.date = date;
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}
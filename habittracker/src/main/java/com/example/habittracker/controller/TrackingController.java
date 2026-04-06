package com.example.habittracker.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.habittracker.dto.MonthlyStatusDTO;
import com.example.habittracker.service.TrackingService;
import com.example.habittracker.service.HabitTrackingService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.security.core.Authentication;
import com.example.habittracker.service.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.Map;


@RestController
@RequestMapping("/track")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService service;
    private final HabitTrackingService habitTrackingService;
    private final UserService userService; 


    @PostMapping
    public void mark(@RequestParam Long habitId,
                    @RequestParam String date,
                    @RequestParam String status,
                    Authentication auth) {

        service.trackHabit(habitId, LocalDate.parse(date), status, auth);
    }

    @GetMapping("/date")
    public Object getByDate(@RequestParam String date) {
        return service.getByDate(LocalDate.parse(date));
    }

    @GetMapping("/month")
    public List<MonthlyStatusDTO> getMonth(
            @RequestParam int month,
            @RequestParam int year,
            Authentication auth) {

        Long userId = userService.getUserId(auth.getName());

        return habitTrackingService.getMonthlyStatus(userId, month, year);
    }

    @GetMapping("/monthly-streak")
    public Map<Long, Integer> getMonthlyStreak(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return service.getMonthlyLongestStreak(month, year);
    }
}
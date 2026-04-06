package com.example.habittracker.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.habittracker.entity.Habit;
import com.example.habittracker.service.HabitService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService service;

    @PostMapping
    public Habit create(@RequestBody Habit habit) {
        return service.createHabit(habit);
    }

    @GetMapping
    public List<Habit> get() {
        return service.getUserHabits();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteHabit(id);
    }

}
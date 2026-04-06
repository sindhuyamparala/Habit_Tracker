package com.example.habittracker.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.habittracker.entity.Habit;
import com.example.habittracker.entity.User;
import com.example.habittracker.repository.HabitRepository;
import com.example.habittracker.repository.UserRepository;
import com.example.habittracker.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    public Habit createHabit(Habit habit) {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow();
        habit.setUser(user);
        return habitRepository.save(habit);
    }

    public List<Habit> getUserHabits() {
        String email = SecurityContextHolder.getContext()
                            .getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                        .orElseThrow();

        System.out.println("LOGGED USER EMAIL: " + email);
        System.out.println("USER ID: " + user.getId());
        
        return habitRepository.findByUser(user);

    }

    public void deleteHabit(Long id) {
    habitRepository.deleteById(id);
    }

    public Habit saveHabit(Habit habit) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        habit.setUser(user);

        return habitRepository.save(habit);
    }
}
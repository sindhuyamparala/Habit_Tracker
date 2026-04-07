package com.example.habittracker.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.habittracker.entity.Habit;
import com.example.habittracker.entity.User;
import com.example.habittracker.repository.HabitRepository;
import com.example.habittracker.repository.HabitTrackingRepository;
import com.example.habittracker.repository.UserRepository;
import com.example.habittracker.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitTrackingRepository habitTrackingRepository;
    private final UserRepository userRepository;

    public Habit createHabit(Habit habit) {
        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        habit.setUser(user);

        return habitRepository.save(habit);
    }

    public List<Habit> getUserHabits() {
        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return habitRepository.findByUser(user);
    }


    @Transactional
    public void deleteHabit(Long id) {
        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Habit habit = habitRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        habitTrackingRepository.deleteByHabit(habit);

        habitRepository.delete(habit);
    }

    public Habit saveHabit(Habit habit) {
        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        habit.setUser(user);

        return habitRepository.save(habit);
    }
}
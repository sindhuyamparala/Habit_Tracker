package com.example.habittracker.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.habittracker.entity.User;
import com.example.habittracker.entity.Habit;
import com.example.habittracker.entity.HabitTracking;

public interface HabitTrackingRepository extends JpaRepository<HabitTracking, Long> {

    List<HabitTracking> findByHabitUserIdAndDate(Long userId, LocalDate date);

    List<HabitTracking> findByHabitUserIdAndDateBetween(
        Long userId, LocalDate start, LocalDate end);

    Optional<HabitTracking> findByHabitIdAndDate(Long habitId, LocalDate date);

    //Optional<HabitTracking> findByHabitIdAndDateAndUser(Long habitId, LocalDate date, User user);

    Optional<HabitTracking> findByHabitAndDate(Habit habit, LocalDate date);

    Optional<HabitTracking> findByHabitIdAndDateAndUser(Long habitId, LocalDate date, User user);

    List<HabitTracking> findByHabitIdAndUserOrderByDateAsc(Long habitId, User user);

    List<HabitTracking> findByHabit_IdAndUserOrderByDateAsc(Long habitId, User user);

    List<HabitTracking> findByHabit_IdAndUserAndDateBetweenOrderByDateAsc(Long habitId, User user, LocalDate start, LocalDate end);
}
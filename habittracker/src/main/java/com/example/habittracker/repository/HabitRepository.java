package com.example.habittracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.habittracker.entity.Habit;
import com.example.habittracker.entity.User;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUser(User user);

    List<Habit> findByUserId(Long userId);

    int countByUserId(Long userId);

    Optional<Habit> findByIdAndUser(Long id, User user);
}
package com.example.habittracker.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.habittracker.dto.MonthlyResponse;
import com.example.habittracker.entity.Habit;
import com.example.habittracker.entity.HabitTracking;
import com.example.habittracker.entity.User;
import com.example.habittracker.repository.HabitRepository;
import com.example.habittracker.repository.HabitTrackingRepository;
import com.example.habittracker.repository.UserRepository;
import com.example.habittracker.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final HabitTrackingRepository trackingRepo;
    private final HabitRepository habitRepo;
    private final UserRepository userRepository;

    public void trackHabit(Long habitId, LocalDate date, String status, Authentication auth) {

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Habit habit = habitRepo.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        Optional<HabitTracking> existing =
                trackingRepo.findByHabitIdAndDateAndUser(habitId, date, user);

        HabitTracking tracking;

        if (existing.isPresent()) {
            tracking = existing.get();
            tracking.setStatus(status);
        } else {
            tracking = new HabitTracking();
            tracking.setHabit(habit);
            tracking.setUser(user);
            tracking.setDate(date);
            tracking.setStatus(status);
        }

        trackingRepo.save(tracking);

        System.out.println("Saved tracking for: " + date + " → " + status);
    }

    public void markHabit(Long habitId, LocalDate date, String status) {
        String email = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Habit habit = habitRepo.findByIdAndUser(habitId, user)
            .orElseThrow(() -> new RuntimeException("Habit not found"));

        HabitTracking tracking = new HabitTracking();
        tracking.setHabit(habit);
        tracking.setDate(date);
        tracking.setStatus(status);

        trackingRepo.save(tracking);
    }

    public List<HabitTracking> getByDate(LocalDate date) {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow();
        return trackingRepo.findByHabitUserIdAndDate(user.getId(), date);
    }

    public List<MonthlyResponse> getMonthlyData(int month, int year) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<HabitTracking> data = trackingRepo
                .findByHabitUserIdAndDateBetween(user.getId(), start, end);

        Map<LocalDate, List<HabitTracking>> grouped =
                data.stream().collect(Collectors.groupingBy(HabitTracking::getDate));

        List<MonthlyResponse> result = new ArrayList<>();

        for (Map.Entry<LocalDate, List<HabitTracking>> entry : grouped.entrySet()) {

            boolean completed = entry.getValue().stream()
                    .anyMatch(t -> t.getStatus().equals("DONE"));

            result.add(new MonthlyResponse(
                    entry.getKey().toString(),
                    completed
            ));
        }

        return result;
    }

    public Map<Long, Integer> getMonthlyLongestStreak(Long userId, int month, int year) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<HabitTracking> records =
                trackingRepo.findByHabitUserIdAndDateBetween(userId, start, end);

        Map<Long, List<HabitTracking>> grouped = new HashMap<>();

        for (HabitTracking ht : records) {
            Long habitId = ht.getHabit().getId();
            grouped.computeIfAbsent(habitId, k -> new ArrayList<>()).add(ht);
        }

        Map<Long, Integer> result = new HashMap<>();

        for (Long habitId : grouped.keySet()) {

            List<HabitTracking> list = grouped.get(habitId);

            list.sort(Comparator.comparing(HabitTracking::getDate));

            int maxStreak = 0;
            int currentStreak = 0;

            for (HabitTracking ht : list) {

                if (ht.getStatus() != null && ht.getStatus().equalsIgnoreCase("DONE")) {
                    currentStreak++;
                    maxStreak = Math.max(maxStreak, currentStreak);
                } else{
                    currentStreak = 0;
                }
            }

            result.put(habitId, maxStreak);
        }

        return result;
    }
}
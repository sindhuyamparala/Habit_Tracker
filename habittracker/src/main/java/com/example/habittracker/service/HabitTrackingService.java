package com.example.habittracker.service;

//import com.example.habittracker.entity.Habit;
import com.example.habittracker.entity.HabitTracking;
import com.example.habittracker.repository.HabitRepository;
import com.example.habittracker.repository.HabitTrackingRepository;
import com.example.habittracker.dto.MonthlyStatusDTO;
//import com.example.habittracker.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
//import org.springframework.security.core.Authentication;
//import com.example.habittracker.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitTrackingService {

    private final HabitTrackingRepository repository;
    private final HabitRepository habitRepository;
    //private final UserRepository userRepository;
    //private final HabitTrackingRepository trackingRepository;

    public List<MonthlyStatusDTO> getMonthlyStatus(Long userId, int month, int year) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<HabitTracking> data =
                repository.findByHabitUserIdAndDateBetween(userId, start, end);

        Map<LocalDate, List<HabitTracking>> grouped =
                data.stream().collect(Collectors.groupingBy(HabitTracking::getDate));

        List<MonthlyStatusDTO> result = new ArrayList<>();

        for (LocalDate date : grouped.keySet()) {

            List<HabitTracking> habits = grouped.get(date);

            int totalHabits = habitRepository.countByUserId(userId);

            long doneCount = habits.stream()
                .filter(h -> h.getStatus().equals("DONE"))
                .count();

            String status;

            if (doneCount == totalHabits) status = "GREEN";
            else if (doneCount > 0) status = "YELLOW";
            else status = "RED";

            result.add(new MonthlyStatusDTO(date, status));
        }

        return result;
    }
}
package com.beotkkotthon.areyousleeping.service;

import com.beotkkotthon.areyousleeping.domain.AllNighters;
import com.beotkkotthon.areyousleeping.dto.response.AllNightersDto;
import com.beotkkotthon.areyousleeping.repository.AllNightersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AllNightersService {
    private final AllNightersRepository allNightersRepository;
    @Transactional(readOnly = true)
    public Map<String, Object> readAllNightersSummary(Long userId, int year, int month) {

        // 시작 날짜와 종료 날짜 설정
        LocalDateTime startOfMonth = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        List<AllNighters> allNightersList = allNightersRepository.findByUserIdAndEndAtBetween(userId, startOfMonth, endOfMonth);

        List<AllNightersDto> allNightersDtos = allNightersList.stream()
                .map(allNighter -> AllNightersDto.builder()
                        .historyTeamId(allNighter.getUserTeam().getId())
                        .startAt(allNighter.getStartAt())
                        .endAt(allNighter.getEndAt())
                        .duration(allNighter.getDuration())
                        .build())
                .toList();

        List<AllNighters> totalAllNightersList = allNightersRepository.findByUserId(userId);
        log.info("total: {}", totalAllNightersList.stream()
                .map(AllNighters::getDuration)
                .filter(Objects::nonNull)
                .toList());
        Integer totalDuration = totalAllNightersList.stream()
                .map(AllNighters::getDuration)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
        Integer totalAllNighters = totalAllNightersList.size();

        Map<String, Object> result = new HashMap<>();
        result.put("totalDuration", totalDuration);
        result.put("totalAllNighters", totalAllNighters);
        result.put("allNightersRecords", allNightersDtos);
        return result;
    }

    @Transactional(readOnly = true)
    public List<AllNightersDto> readAllNightersCalendar(Long userId, int year, int month) {
        // 시작 날짜와 종료 날짜 설정
        LocalDateTime startOfMonth = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        List<AllNighters> allNightersList = allNightersRepository.findByUserIdAndEndAtBetween(userId, startOfMonth, endOfMonth);

        return allNightersList.stream()
                .map(allNighter -> AllNightersDto.builder()
                        .historyTeamId(allNighter.getUserTeam().getHistoryTeamId())
                        .startAt(allNighter.getStartAt())
                        .endAt(allNighter.getEndAt())
                        .duration(allNighter.getDuration())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> readAllNightersHistory(Long userId) {
        List<AllNighters> allNightersList = allNightersRepository.findByUserId(userId);

        int totalDuration = allNightersList.stream()
                .map(AllNighters::getDuration)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
        int totalAllNighters = allNightersList.size();

        Map<String, Object> result = new HashMap<>();
        result.put("totalDuration", totalDuration);
        result.put("totalAllNighters", totalAllNighters);
        return result;
    }
}

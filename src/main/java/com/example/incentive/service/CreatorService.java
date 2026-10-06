package com.example.incentive.service;

import com.example.incentive.exception.CreatorNotFoundException;
import com.example.incentive.model.CreatorStats;
import com.example.incentive.repository.CreatorStatsRepository;
import org.springframework.stereotype.Service;

@Service
public class CreatorService {
    private final CreatorStatsRepository creatorStatsRepository;

    public CreatorService(CreatorStatsRepository creatorStatsRepository) {
        this.creatorStatsRepository = creatorStatsRepository;
    }

    public CreatorStats getSevenDayStats(long creatorId) {
        return creatorStatsRepository.findSevenDayStatsByCreatorId(creatorId)
                .orElseThrow(() -> new CreatorNotFoundException(creatorId));
    }
}
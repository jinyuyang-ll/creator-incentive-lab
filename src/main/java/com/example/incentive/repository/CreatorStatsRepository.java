package com.example.incentive.repository;

import com.example.incentive.model.CreatorStats;

import java.util.Optional;

public interface CreatorStatsRepository {
    Optional<CreatorStats> findSevenDayStatsByCreatorId(long creatorId);
}

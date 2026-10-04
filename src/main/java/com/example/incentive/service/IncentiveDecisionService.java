package com.example.incentive.service;

import com.example.incentive.exception.DecisionNotFoundException;
import com.example.incentive.model.CreatorStats;
import com.example.incentive.model.IncentiveDecision;
import com.example.incentive.repository.InMemoryDecisionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class IncentiveDecisionService {
    private final CreatorService creatorService;
    private final InMemoryDecisionRepository decisionRepository;

    public IncentiveDecisionService(CreatorService creatorService,
                                    InMemoryDecisionRepository decisionRepository) {
        this.creatorService = creatorService;
        this.decisionRepository = decisionRepository;
    }

    public IncentiveDecision createDecision(long creatorId) {
        CreatorStats stats = creatorService.getSevenDayStats(creatorId);

        // 教学规则：已有一定创作意愿，但有效产出还不高时建议激励。
        boolean recommended = stats.getActiveDaysLast7Days() >= 2
                && stats.getValidPostsLast7Days() < 5;
        String recommendation = recommended ? "建议激励" : "暂不激励";
        String reason = String.format("过去7天活跃%d天，有效内容%d条",
                stats.getActiveDaysLast7Days(), stats.getValidPostsLast7Days());

        IncentiveDecision decision = new IncentiveDecision(
                decisionRepository.nextId(), creatorId, recommended,
                recommendation, reason, Instant.now());
        return decisionRepository.save(decision);
    }

    public IncentiveDecision getDecision(long decisionId) {
        return decisionRepository.findById(decisionId)
                .orElseThrow(() -> new DecisionNotFoundException(decisionId));
    }
}

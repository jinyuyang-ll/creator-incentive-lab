package com.example.incentive.repository;

import com.example.incentive.model.IncentiveDecision;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryDecisionRepository {
    private final AtomicLong sequence = new AtomicLong(0);
    private final Map<Long, IncentiveDecision> decisions = new ConcurrentHashMap<>();

    public long nextId() {
        return sequence.incrementAndGet();
    }

    public IncentiveDecision save(IncentiveDecision decision) {
        decisions.put(decision.getDecisionId(), decision);
        return decision;
    }

    public Optional<IncentiveDecision> findById(long id) {
        return Optional.ofNullable(decisions.get(id));
    }
}

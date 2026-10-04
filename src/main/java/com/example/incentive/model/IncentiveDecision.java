package com.example.incentive.model;

import java.time.Instant;

public class IncentiveDecision {
    private final long decisionId;
    private final long creatorId;
    private final boolean recommended;
    private final String recommendation;
    private final String reason;
    private final Instant createdAt;

    public IncentiveDecision(long decisionId, long creatorId, boolean recommended,
                             String recommendation, String reason, Instant createdAt) {
        this.decisionId = decisionId;
        this.creatorId = creatorId;
        this.recommended = recommended;
        this.recommendation = recommendation;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public long getDecisionId() { return decisionId; }
    public long getCreatorId() { return creatorId; }
    public boolean isRecommended() { return recommended; }
    public String getRecommendation() { return recommendation; }
    public String getReason() { return reason; }
    public Instant getCreatedAt() { return createdAt; }
}

package com.example.incentive.controller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class CreateDecisionRequest {
    @NotNull(message = "creatorId is required")
    @Positive(message = "creatorId must be positive")
    private Long creatorId;

    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
}

package com.example.incentive.controller;

import com.example.incentive.model.IncentiveDecision;
import com.example.incentive.service.IncentiveDecisionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/incentive-decisions")
@Validated
public class IncentiveDecisionController {
    private final IncentiveDecisionService decisionService;

    public IncentiveDecisionController(IncentiveDecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncentiveDecision create(@Valid @RequestBody CreateDecisionRequest request) {
        return decisionService.createDecision(request.getCreatorId());
    }

    @GetMapping("/{id}")
    public IncentiveDecision get(@PathVariable @Positive long id) {
        return decisionService.getDecision(id);
    }
}

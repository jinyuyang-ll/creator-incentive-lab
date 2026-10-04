package com.example.incentive.controller;

import com.example.incentive.model.CreatorStats;
import com.example.incentive.service.CreatorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/creators")
@Validated
public class CreatorController {
    private final CreatorService creatorService;

    public CreatorController(CreatorService creatorService) {
        this.creatorService = creatorService;
    }

    @GetMapping("/{id}/stats")
    public CreatorStats getStats(@PathVariable @Positive long id) {
        return creatorService.getSevenDayStats(id);
    }
}

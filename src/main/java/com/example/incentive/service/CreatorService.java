package com.example.incentive.service;

import com.example.incentive.exception.CreatorNotFoundException;
import com.example.incentive.model.CreatorStats;
import com.example.incentive.model.PostActivity;
import com.example.incentive.repository.InMemoryCreatorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CreatorService {
    private final InMemoryCreatorRepository creatorRepository;

    public CreatorService(InMemoryCreatorRepository creatorRepository) {
        this.creatorRepository = creatorRepository;
    }

    public CreatorStats getSevenDayStats(long creatorId) {
        creatorRepository.findCreatorById(creatorId)
                .orElseThrow(() -> new CreatorNotFoundException(creatorId));

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6); // 包含今天，共7个自然日
        List<PostActivity> posts = creatorRepository.findPostsByCreatorId(creatorId);

        long postCount = posts.stream()
                .filter(post -> inRange(post.getPostDate(), startDate, today))
                .count();
        long activeDays = posts.stream()
                .filter(post -> inRange(post.getPostDate(), startDate, today))
                .map(PostActivity::getPostDate)
                .distinct()
                .count();
        long validPostCount = posts.stream()
                .filter(post -> inRange(post.getPostDate(), startDate, today))
                .filter(PostActivity::isValid)
                .count();

        return new CreatorStats(creatorId, postCount, activeDays, validPostCount);
    }

    private boolean inRange(LocalDate date, LocalDate start, LocalDate end) {
        return !date.isBefore(start) && !date.isAfter(end);
    }
}

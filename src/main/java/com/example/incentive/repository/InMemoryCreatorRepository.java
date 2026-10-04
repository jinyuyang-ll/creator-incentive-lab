package com.example.incentive.repository;

import com.example.incentive.model.Creator;
import com.example.incentive.model.PostActivity;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryCreatorRepository {
    private final Map<Long, Creator> creators = new HashMap<>();
    private final List<PostActivity> posts;

    public InMemoryCreatorRepository() {
        LocalDate today = LocalDate.now();
        creators.put(1L, new Creator(1L, today.minusDays(100)));
        creators.put(2L, new Creator(2L, today.minusDays(30)));
        creators.put(3L, new Creator(3L, today.minusDays(10)));

        posts = Arrays.asList(
                new PostActivity(1L, today, true),
                new PostActivity(1L, today.minusDays(1), true),
                new PostActivity(1L, today.minusDays(1), false),
                new PostActivity(1L, today.minusDays(4), true),
                new PostActivity(1L, today.minusDays(7), true),
                new PostActivity(2L, today.minusDays(2), false),
                new PostActivity(2L, today.minusDays(8), true)
        );
    }

    public Optional<Creator> findCreatorById(long id) {
        return Optional.ofNullable(creators.get(id));
    }

    public List<PostActivity> findPostsByCreatorId(long creatorId) {
        return Collections.unmodifiableList(posts.stream()
                .filter(post -> post.getCreatorId() == creatorId)
                .collect(Collectors.toList()));
    }
}

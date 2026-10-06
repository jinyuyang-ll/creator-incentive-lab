package com.example.incentive.repository;

import com.example.incentive.model.CreatorStats;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("!test")
public class JdbcCreatorRepository implements CreatorStatsRepository {
    private static final String SEVEN_DAY_STATS_SQL =
            "SELECT c.creator_id, " +
            "COUNT(p.creator_id) AS posts_last_7_days, " +
            "COUNT(DISTINCT p.post_date) AS active_days_last_7_days, " +
            "SUM(CASE WHEN p.is_valid = TRUE THEN 1 ELSE 0 END) AS valid_posts_last_7_days " +
            "FROM creators c " +
            "LEFT JOIN posts p ON p.creator_id = c.creator_id " +
            "AND p.post_date BETWEEN DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY) AND CURRENT_DATE " +
            "WHERE c.creator_id = ? " +
            "GROUP BY c.creator_id";

    private final JdbcTemplate jdbcTemplate;

    public JdbcCreatorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<CreatorStats> findSevenDayStatsByCreatorId(long creatorId) {
        List<CreatorStats> results = jdbcTemplate.query(
                SEVEN_DAY_STATS_SQL,
                (resultSet, rowNum) -> new CreatorStats(
                        resultSet.getLong("creator_id"),
                        resultSet.getLong("posts_last_7_days"),
                        resultSet.getLong("active_days_last_7_days"),
                        resultSet.getLong("valid_posts_last_7_days")
                ),
                creatorId
        );
        return results.stream().findFirst();
    }
}

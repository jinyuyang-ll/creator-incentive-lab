package com.example.incentive.model;

public class CreatorStats {
    private final long creatorId;
    private final long postsLast7Days;
    private final long activeDaysLast7Days;
    private final long validPostsLast7Days;

    public CreatorStats(long creatorId, long postsLast7Days,
                        long activeDaysLast7Days, long validPostsLast7Days) {
        this.creatorId = creatorId;
        this.postsLast7Days = postsLast7Days;
        this.activeDaysLast7Days = activeDaysLast7Days;
        this.validPostsLast7Days = validPostsLast7Days;
    }

    public long getCreatorId() { return creatorId; }
    public long getPostsLast7Days() { return postsLast7Days; }
    public long getActiveDaysLast7Days() { return activeDaysLast7Days; }
    public long getValidPostsLast7Days() { return validPostsLast7Days; }
}

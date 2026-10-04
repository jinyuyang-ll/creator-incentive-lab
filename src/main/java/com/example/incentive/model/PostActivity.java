package com.example.incentive.model;

import java.time.LocalDate;

public class PostActivity {
    private final long creatorId;
    private final LocalDate postDate;
    private final boolean valid;

    public PostActivity(long creatorId, LocalDate postDate, boolean valid) {
        this.creatorId = creatorId;
        this.postDate = postDate;
        this.valid = valid;
    }

    public long getCreatorId() { return creatorId; }
    public LocalDate getPostDate() { return postDate; }
    public boolean isValid() { return valid; }
}

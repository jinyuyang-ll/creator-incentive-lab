package com.example.incentive.model;

import java.time.LocalDate;

public class Creator {
    private final long id;
    private final LocalDate joinDate;

    public Creator(long id, LocalDate joinDate) {
        this.id = id;
        this.joinDate = joinDate;
    }

    public long getId() { return id; }
    public LocalDate getJoinDate() { return joinDate; }
}

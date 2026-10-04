package com.example.incentive.controller;

import java.time.Instant;
import java.util.List;

public class ApiError {
    private final Instant timestamp = Instant.now();
    private final int status;
    private final String error;
    private final List<String> details;
    private final String path;
    private final String requestId;

    public ApiError(int status, String error, List<String> details, String path, String requestId) {
        this.status = status;
        this.error = error;
        this.details = details;
        this.path = path;
        this.requestId = requestId;
    }

    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public List<String> getDetails() { return details; }
    public String getPath() { return path; }
    public String getRequestId() { return requestId; }
}

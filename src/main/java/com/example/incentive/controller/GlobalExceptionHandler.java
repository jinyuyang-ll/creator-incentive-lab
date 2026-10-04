package com.example.incentive.controller;

import com.example.incentive.exception.CreatorNotFoundException;
import com.example.incentive.exception.DecisionNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({CreatorNotFoundException.class, DecisionNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(RuntimeException exception, HttpServletRequest request) {
        log.warn("Resource not found: {}", exception.getMessage());
        return error(HttpStatus.NOT_FOUND, Collections.singletonList(exception.getMessage()), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidBody(MethodArgumentNotValidException exception,
                                      HttpServletRequest request) {
        List<String> details = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        log.warn("Invalid request body: {}", details);
        return error(HttpStatus.BAD_REQUEST, details, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraint(ConstraintViolationException exception,
                                     HttpServletRequest request) {
        List<String> details = Collections.singletonList(exception.getMessage());
        log.warn("Invalid request parameter: {}", exception.getMessage());
        return error(HttpStatus.BAD_REQUEST, details, request);
    }

    private ApiError error(HttpStatus status, List<String> details, HttpServletRequest request) {
        String requestId = (String) request.getAttribute("requestId");
        return new ApiError(status.value(), status.getReasonPhrase(), details,
                request.getRequestURI(), requestId);
    }
}

package io.github.lbernau.bistro.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class OrderValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public OrderValidationException(Map<String, String> errors) {
        super("Order validation failed.");
        this.errors = errors;
    }
}

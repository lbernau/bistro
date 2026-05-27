package io.github.lbernau.bistro.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class CsvImportException extends RuntimeException {

    private final List<String> errors;

    public CsvImportException(List<String> errors) {
        super(String.join("\n", errors));
        this.errors = errors;
    }

}

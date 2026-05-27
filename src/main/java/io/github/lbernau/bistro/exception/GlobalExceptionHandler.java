package io.github.lbernau.bistro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        final Map<String, String> errors = new HashMap<>();
        exception.getBindingResult()
                 .getAllErrors()
                 .forEach((error) -> {
                     String fieldName = ((FieldError) error).getField();
                     String errorMessage = error.getDefaultMessage();
                     errors.put(fieldName, errorMessage);
                 });

        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation failed");
        problemDetail.setDetail(exception.getDetailMessageCode());
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    @ExceptionHandler(value = OrderValidationException.class)
    public ProblemDetail handleOrderValidationException(OrderValidationException exception) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Order validation failed");
        problemDetail.setDetail(exception.getMessage());
        problemDetail.setProperty("errors", exception.getErrors());

        return problemDetail;
    }
}

package com.baylorw.branchtest.exception;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Hidden // Without this, this exception handler breaks Swagger. :(
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({ResponseStatusException.class})
    public ResponseEntity<Object> handleNotFoundException(ResponseStatusException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getReason());
    }

    @ExceptionHandler({ResourceAccessException.class})
    public ResponseEntity<Object> handleNotFoundException(ResourceAccessException exception) {
        // TODO: This is a 5xx error but Java doesn't return the code. Need a way to get the actual value.
        // There are ways (RestTemplate custom error handler, chained .onStatus(), checking error cause object type)
        // but they're a fair amount of work so for this little toy program i didn't do them.
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(exception.getMessage());
    }
}

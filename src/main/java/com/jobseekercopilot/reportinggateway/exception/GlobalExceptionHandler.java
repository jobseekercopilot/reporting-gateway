package com.jobseekercopilot.reportinggateway.exception;

import com.jobseekercopilot.reportinggateway.controller.ReportingGatewayController.MissingUserIdException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MissingUserIdException.class)
    ResponseEntity<Map<String, String>> missingUserId(MissingUserIdException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "UNAUTHORIZED", "message", "Missing X-User-Id header"));
    }

    @ExceptionHandler(RestClientException.class)
    ResponseEntity<Map<String, String>> downstreamFailure(RestClientException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "DOWNSTREAM_FAILURE", "message", "Reporting service failed"));
    }
}

package com.jobseekercopilot.reportinggateway.controller;

import com.jobseekercopilot.generated.reportingservice.model.ReportingSummaryResponse;
import com.jobseekercopilot.generated.reportingservice.model.UcJournalResponse;
import com.jobseekercopilot.reportinggateway.service.ReportingGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportingGatewayController {
    private static final String USER_ID_HEADER = "X-User-Id";
    private final ReportingGatewayService reportingGatewayService;

    public ReportingGatewayController(ReportingGatewayService reportingGatewayService) {
        this.reportingGatewayService = reportingGatewayService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get reporting summary")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporting summary returned"),
            @ApiResponse(responseCode = "401", description = "Missing X-User-Id header"),
            @ApiResponse(responseCode = "502", description = "Reporting service failed")
    })
    public ResponseEntity<ReportingSummaryResponse> summary(
            @Parameter(in = ParameterIn.HEADER, name = USER_ID_HEADER, required = false)
            @RequestHeader(name = USER_ID_HEADER, required = false) String userId) {
        validateUserId(userId);
        return ResponseEntity.ok(reportingGatewayService.summary(userId));
    }

    @GetMapping("/uc-journal")
    @Operation(summary = "Get deterministic Universal Credit journal text")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "UC journal text returned"),
            @ApiResponse(responseCode = "401", description = "Missing X-User-Id header"),
            @ApiResponse(responseCode = "502", description = "Reporting service failed")
    })
    public ResponseEntity<UcJournalResponse> ucJournal(
            @Parameter(in = ParameterIn.HEADER, name = USER_ID_HEADER, required = false)
            @RequestHeader(name = USER_ID_HEADER, required = false) String userId) {
        validateUserId(userId);
        return ResponseEntity.ok(reportingGatewayService.ucJournal(userId));
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new MissingUserIdException();
        }
    }

    public static class MissingUserIdException extends RuntimeException {
    }
}

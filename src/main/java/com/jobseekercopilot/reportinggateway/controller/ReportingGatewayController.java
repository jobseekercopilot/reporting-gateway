package com.jobseekercopilot.reportinggateway.controller;

import com.jobseekercopilot.reportinggateway.dto.ReportingSummaryResponse;
import com.jobseekercopilot.reportinggateway.dto.UcJournalResponse;
import com.jobseekercopilot.reportinggateway.service.ReportingGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportingGatewayController {
    private final ReportingGatewayService reportingGatewayService;

    public ReportingGatewayController(ReportingGatewayService reportingGatewayService) {
        this.reportingGatewayService = reportingGatewayService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get reporting summary")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporting summary returned"),
            @ApiResponse(responseCode = "401", description = "Valid access token required"),
            @ApiResponse(responseCode = "502", description = "Reporting service failed")
    })
    public ResponseEntity<ReportingSummaryResponse> summary(
            @AuthenticationPrincipal Jwt accessToken) {
        return ResponseEntity.ok(reportingGatewayService.summary(
                accessToken.getSubject(),
                accessToken.getTokenValue()));
    }

    @GetMapping("/uc-journal")
    @Operation(summary = "Get deterministic Universal Credit journal text")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "UC journal text returned"),
            @ApiResponse(responseCode = "401", description = "Valid access token required"),
            @ApiResponse(responseCode = "502", description = "Reporting service failed")
    })
    public ResponseEntity<UcJournalResponse> ucJournal(
            @AuthenticationPrincipal Jwt accessToken) {
        return ResponseEntity.ok(reportingGatewayService.ucJournal(
                accessToken.getSubject(),
                accessToken.getTokenValue()));
    }
}

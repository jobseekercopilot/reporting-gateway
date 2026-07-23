package com.jobseekercopilot.reportinggateway.service;

import com.jobseekercopilot.generated.reportingservice.api.ReportingControllerApi;
import com.jobseekercopilot.generated.reportingservice.model.ReportingSummaryResponse;
import com.jobseekercopilot.generated.reportingservice.model.UcJournalResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReportingGatewayService {
    private static final Logger log = LoggerFactory.getLogger(ReportingGatewayService.class);

    private final ReportingControllerApi reportingControllerApi;

    public ReportingGatewayService(ReportingControllerApi reportingControllerApi) {
        this.reportingControllerApi = reportingControllerApi;
    }

    public ReportingSummaryResponse summary(String userId) {
        long startedAt = System.nanoTime();
        log.info("reporting-gateway summary request userId={}", userId);
        ReportingSummaryResponse response = reportingControllerApi.summary(userId);
        log.info("reporting-service summary returned userId={} applicationTotal={} durationMs={}",
                userId,
                response == null || response.getApplicationSummary() == null ? null : response.getApplicationSummary().getTotal(),
                (System.nanoTime() - startedAt) / 1_000_000);
        return response;
    }

    public UcJournalResponse ucJournal(String userId) {
        long startedAt = System.nanoTime();
        log.info("reporting-gateway UC journal request userId={}", userId);
        UcJournalResponse response = reportingControllerApi.ucJournal(userId);
        log.info("reporting-service UC journal returned userId={} durationMs={}",
                userId,
                (System.nanoTime() - startedAt) / 1_000_000);
        return response;
    }
}

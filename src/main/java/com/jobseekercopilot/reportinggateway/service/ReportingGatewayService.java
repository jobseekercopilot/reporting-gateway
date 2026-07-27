package com.jobseekercopilot.reportinggateway.service;

import com.jobseekercopilot.reportinggateway.dto.ReportingSummaryResponse;
import com.jobseekercopilot.reportinggateway.dto.UcJournalResponse;
import com.jobseekercopilot.reportinggateway.security.ReportingGatewayCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReportingGatewayService {
    private static final Logger log = LoggerFactory.getLogger(ReportingGatewayService.class);

    private final RestTemplate restTemplate;
    private final ReportingGatewayCredentials credentials;
    private final String reportingServiceBaseUrl;

    public ReportingGatewayService(
            RestTemplate restTemplate,
            ReportingGatewayCredentials credentials,
            @Value("${services.reporting-service.base-url}") String reportingServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.credentials = credentials;
        this.reportingServiceBaseUrl = reportingServiceBaseUrl;
    }

    public ReportingSummaryResponse summary(String owner, String accessToken) {
        long startedAt = System.nanoTime();
        log.info("Reporting summary request started");
        ReportingSummaryResponse response = restTemplate.exchange(
                reportingServiceBaseUrl + "/api/v1/reports/summary",
                HttpMethod.GET,
                request(owner, accessToken),
                ReportingSummaryResponse.class).getBody();
        log.info("Reporting summary request completed applicationTotal={} durationMs={}",
                response == null || response.applicationSummary() == null
                        ? null
                        : response.applicationSummary().total(),
                (System.nanoTime() - startedAt) / 1_000_000);
        return response;
    }

    public UcJournalResponse ucJournal(String owner, String accessToken) {
        long startedAt = System.nanoTime();
        log.info("Reporting journal request started");
        UcJournalResponse response = restTemplate.exchange(
                reportingServiceBaseUrl + "/api/v1/reports/uc-journal",
                HttpMethod.GET,
                request(owner, accessToken),
                UcJournalResponse.class).getBody();
        log.info("Reporting journal request completed durationMs={}",
                (System.nanoTime() - startedAt) / 1_000_000);
        return response;
    }

    private HttpEntity<Void> request(String owner, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("X-Service-Token", credentials.reportingServiceToken());
        headers.set("X-Report-Owner", owner);
        return new HttpEntity<>(headers);
    }
}

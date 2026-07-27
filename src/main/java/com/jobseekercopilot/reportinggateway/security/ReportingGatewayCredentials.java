package com.jobseekercopilot.reportinggateway.security;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReportingGatewayCredentials {
    static final int MINIMUM_TOKEN_BYTES = 32;

    private final String reportingServiceToken;

    public ReportingGatewayCredentials(
            @Value("${reporting-gateway.security.reporting-service-token}")
            String reportingServiceToken) {
        if (reportingServiceToken == null
                || reportingServiceToken.isBlank()
                || reportingServiceToken.getBytes(StandardCharsets.UTF_8).length
                < MINIMUM_TOKEN_BYTES) {
            throw new IllegalStateException(
                    "Reporting Service token must contain at least 32 bytes.");
        }
        this.reportingServiceToken = reportingServiceToken;
    }

    public String reportingServiceToken() {
        return reportingServiceToken;
    }
}

package com.jobseekercopilot.reportinggateway.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ReportingGatewayCredentialsTest {
    private static final String TOKEN =
            "test-only-reporting-service-token-32-bytes";

    @Test
    void acceptsStrongRuntimeInjectedCredential() {
        assertEquals(
                TOKEN,
                new ReportingGatewayCredentials(TOKEN).reportingServiceToken());
    }

    @Test
    void rejectsMissingOrShortCredentialWithoutReflectingIt() {
        IllegalStateException missing = assertThrows(
                IllegalStateException.class,
                () -> new ReportingGatewayCredentials(""));
        IllegalStateException shortToken = assertThrows(
                IllegalStateException.class,
                () -> new ReportingGatewayCredentials("short"));

        assertEquals(
                "Reporting Service token must contain at least 32 bytes.",
                missing.getMessage());
        assertEquals(missing.getMessage(), shortToken.getMessage());
    }
}

package com.jobseekercopilot.reportinggateway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.jobseekercopilot.reportinggateway.dto.ReportingSummaryResponse;
import com.jobseekercopilot.reportinggateway.security.ReportingGatewayCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

class ReportingGatewayServiceTest {
    private static final String SERVICE_TOKEN =
            "test-only-reporting-service-token-32-bytes";

    @Test
    void sendsValidatedOwnerAccessTokenAndDedicatedServiceIdentity() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("http://reporting-service:8096/api/v1/reports/summary"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer access-token"))
                .andExpect(header("X-Service-Token", SERVICE_TOKEN))
                .andExpect(header("X-Report-Owner", "subject-123"))
                .andRespond(withSuccess(
                        """
                        {
                          "userId": "subject-123",
                          "applicationSummary": {"applied": 2, "total": 2},
                          "activityTimeline": [],
                          "ucJournalPreview": "Applied"
                        }
                        """,
                        MediaType.APPLICATION_JSON));
        ReportingGatewayService service = new ReportingGatewayService(
                restTemplate,
                new ReportingGatewayCredentials(SERVICE_TOKEN),
                "http://reporting-service:8096");

        ReportingSummaryResponse result = service.summary(
                "subject-123",
                "access-token");

        assertThat(result.userId()).isEqualTo("subject-123");
        assertThat(result.applicationSummary().applied()).isEqualTo(2);
        server.verify();
    }
    @Test
    void sendsDerivedIdentityForEvidenceExport() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("http://reporting-service:8096/api/v1/reports/evidence.txt"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer access-token"))
                .andExpect(header("X-Service-Token", SERVICE_TOKEN))
                .andExpect(header("X-Report-Owner", "subject-123"))
                .andRespond(withSuccess("Persisted evidence", MediaType.TEXT_PLAIN));
        ReportingGatewayService service = new ReportingGatewayService(
                restTemplate,
                new ReportingGatewayCredentials(SERVICE_TOKEN),
                "http://reporting-service:8096");

        assertThat(service.evidenceExport("subject-123", "access-token"))
                .isEqualTo("Persisted evidence");
        server.verify();
    }
}

package com.jobseekercopilot.reportinggateway;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jobseekercopilot.reportinggateway.dto.ReportingSummaryResponse;
import com.jobseekercopilot.reportinggateway.dto.ReportingSummaryResponse.ApplicationSummary;
import com.jobseekercopilot.reportinggateway.config.SecurityConfig;
import com.jobseekercopilot.reportinggateway.service.ReportingGatewayService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(properties =
        "reporting-gateway.security.reporting-service-token="
        + "test-only-reporting-service-token-32-bytes")
@Import(SecurityConfig.class)
class ReportingGatewayControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private ReportingGatewayService reportingGatewayService;

    @Test
    void returnsUnauthorizedWhenAccessTokenMissing() throws Exception {
        mockMvc.perform(get("/api/v1/reports/summary"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"))
                .andExpect(jsonPath("$.message").value("Valid authentication is required."));
    }

    @Test
    void scopesSummaryToJwtSubjectAndIgnoresForgedUserHeader() throws Exception {
        ReportingSummaryResponse response = new ReportingSummaryResponse(
                "subject-123",
                new ApplicationSummary(0, 2, 0, 0, 0, 0, 0, 2),
                List.of(),
                null,
                "05/10/2026 - Applied for Software Developer at Matchtech.");
        when(reportingGatewayService.summary(eq("subject-123"), eq("access-token")))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/reports/summary")
                        .header("X-User-Id", "forged-user")
                        .with(jwt().jwt(token -> token
                                .subject("subject-123")
                                .tokenValue("access-token")
                                .claim("token_type", "access"))))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(jsonPath("$.userId").value("subject-123"))
                .andExpect(jsonPath("$.applicationSummary.applied").value(2))
                .andExpect(jsonPath("$.ucJournalPreview").value("05/10/2026 - Applied for Software Developer at Matchtech."));
    }

    @Test
    void runtimeOpenApiIsDisabledByDefault() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isNotFound());
    }
}

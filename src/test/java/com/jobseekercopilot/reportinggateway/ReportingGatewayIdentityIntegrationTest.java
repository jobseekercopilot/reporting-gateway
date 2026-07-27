package com.jobseekercopilot.reportinggateway;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jobseekercopilot.reportinggateway.dto.ReportingSummaryResponse;
import com.jobseekercopilot.reportinggateway.dto.ReportingSummaryResponse.ApplicationSummary;
import com.jobseekercopilot.reportinggateway.service.ReportingGatewayService;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(properties =
        "reporting-gateway.security.reporting-service-token="
        + "test-only-reporting-service-token-32-bytes")
@AutoConfigureMockMvc
class ReportingGatewayIdentityIntegrationTest {
    private static final TestJwksServer JWKS = new TestJwksServer();

    @DynamicPropertySource
    static void jwtProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "reporting-gateway.security.jwk-set-uri",
                JWKS::jwkSetUri);
        registry.add(
                "reporting-gateway.security.issuer",
                () -> TestJwksServer.ISSUER);
        registry.add(
                "reporting-gateway.security.audience",
                () -> TestJwksServer.AUDIENCE);
    }

    @AfterAll
    static void stopJwks() {
        JWKS.close();
    }

    @Autowired private MockMvc mockMvc;
    @MockBean private ReportingGatewayService reportingGatewayService;

    @Test
    void browserIdentityHeaderCannotAuthenticateOrOverrideJwtSubject()
            throws Exception {
        mockMvc.perform(get("/api/v1/reports/summary")
                        .header("X-User-Id", "victim"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));

        String token = JWKS.validToken("alice");
        when(reportingGatewayService.summary(eq("alice"), eq(token)))
                .thenReturn(new ReportingSummaryResponse(
                        "alice",
                        new ApplicationSummary(0, 1, 0, 0, 0, 0, 0, 1),
                        List.of(),
                        null,
                        "Applied"));

        mockMvc.perform(get("/api/v1/reports/summary")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .header("X-User-Id", "victim"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("alice"));

        verify(reportingGatewayService).summary("alice", token);
    }

    @Test
    void missingInvalidExpiredForgedAndWrongPurposeTokensFailUniformly()
            throws Exception {
        List<String> invalidTokens = List.of(
                "not-a-jwt",
                JWKS.expiredToken("expired"),
                JWKS.forgedKnownKeyToken("forged"),
                JWKS.wrongIssuerToken("wrong-issuer"),
                JWKS.wrongAudienceToken("wrong-audience"),
                JWKS.refreshTokenType("wrong-type"),
                JWKS.missingSubjectToken());

        assertAuthenticationFailure(null);
        for (String token : invalidTokens) {
            assertAuthenticationFailure(token);
        }
    }

    private void assertAuthenticationFailure(String token) throws Exception {
        var request = get("/api/v1/reports/summary");
        if (token != null) {
            request.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"))
                .andExpect(jsonPath("$.message")
                        .value("Valid authentication is required."))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        if (token != null) {
            assertFalse(response.contains(token));
        }
        assertFalse(response.contains("127.0.0.1"));
        assertFalse(response.contains("subject"));
        assertFalse(response.contains("Jwt"));
    }
}

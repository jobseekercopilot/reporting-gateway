package com.jobseekercopilot.reportinggateway;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jobseekercopilot.generated.reportingservice.model.ApplicationSummary;
import com.jobseekercopilot.generated.reportingservice.model.ReportingSummaryResponse;
import com.jobseekercopilot.reportinggateway.service.ReportingGatewayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
class ReportingGatewayControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private ReportingGatewayService reportingGatewayService;

    @Test
    void returnsUnauthorizedWhenUserHeaderMissing() throws Exception {
        mockMvc.perform(get("/api/v1/reports/summary"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Missing X-User-Id header"));
    }

    @Test
    void returnsSummaryFromReportingService() throws Exception {
        ReportingSummaryResponse response = new ReportingSummaryResponse()
                .userId("user-123")
                .applicationSummary(new ApplicationSummary().applied(2).total(2))
                .ucJournalPreview("05/10/2026 - Applied for Software Developer at Matchtech.");
        when(reportingGatewayService.summary(eq("user-123"))).thenReturn(response);

        mockMvc.perform(get("/api/v1/reports/summary").header("X-User-Id", "user-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.applicationSummary.applied").value(2))
                .andExpect(jsonPath("$.ucJournalPreview").value("05/10/2026 - Applied for Software Developer at Matchtech."));
    }
}

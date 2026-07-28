package com.jobseekercopilot.reportinggateway.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ReportingSummaryResponse(
        String userId,
        ApplicationSummary applicationSummary,
        List<ActivityTimelineItem> activityTimeline,
        CommitmentProgress commitmentProgress,
        String ucJournalPreview) {

    public record ApplicationSummary(
            int documentsGenerated,
            int applied,
            int interview,
            int unsuccessful,
            int offer,
            int accepted,
            int rejectedByUser,
            int total) {
    }

    public record ActivityTimelineItem(
            String applicationId,
            LocalDateTime occurredAt,
            String eventType,
            String evidenceCategory,
            String status,
            String provider,
            String jobTitle,
            String companyName,
            String text) {
    }

    public record CommitmentProgress(
            BigDecimal requiredHours,
            BigDecimal completedHours,
            BigDecimal remainingHours,
            int percentageComplete,
            LocalDate periodStart,
            LocalDate periodEnd,
            String remainingText) {
    }
}

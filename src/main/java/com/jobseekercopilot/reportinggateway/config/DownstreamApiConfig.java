package com.jobseekercopilot.reportinggateway.config;

import com.jobseekercopilot.generated.reportingservice.api.ReportingControllerApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DownstreamApiConfig {
    @Bean
    ReportingControllerApi reportingControllerApi(@Value("${services.reporting-service.base-url}") String baseUrl) {
        var client = new com.jobseekercopilot.generated.reportingservice.client.ApiClient();
        client.setBasePath(baseUrl);
        return new ReportingControllerApi(client);
    }
}

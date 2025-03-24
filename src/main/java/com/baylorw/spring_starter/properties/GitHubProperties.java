package com.baylorw.spring_starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "github")
public record GitHubProperties(
        String url,
        GitHubEndpointsProperties endpoints
) {
}

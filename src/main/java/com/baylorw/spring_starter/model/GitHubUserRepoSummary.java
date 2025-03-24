package com.baylorw.spring_starter.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * This is the data we send back to our caller. It summarizes data from our calls to GitHub.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GitHubUserRepoSummary(
        String name,
        String url // from html_url
) {
}

package com.baylorw.spring_starter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * This is the data we send back to our caller. It summarizes data from our calls to GitHub.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)   // When serialized to JSON, use snake_case
@Data
@Builder
@NoArgsConstructor  // For JSON serializer
@AllArgsConstructor // For Builder
public class GitHubUserSummary {
    private String userName;    // from login
    private String avatar; // from avatarUrl
    private String url;
    private String displayName; // from name
    private String geoLocation; // from location
    private String email;
    private String createdAt;
    private List<GitHubUserRepoSummary> repos;
}

/* EXAMPLE
{
    user_name: "octocat",
    display_name: "The Octocat",
    avatar: "https://avatars3.githubusercontent.com/u/583231?v=4",
    geo_location: "San Francisco",
    email: null,
    url: "https://github.com/octocat ",
    created_at: "2011-01-25 18:44:36",
    repos: [{
        name: "boysenberry-repo-1",
        url: "https://github.com/octocat/boysenberry-repo-1"
    }, ...
    ]
}
 */
package com.baylorw.spring_starter

import com.baylorw.spring_starter.model.GitHubUser
import com.baylorw.spring_starter.model.GitHubUserRepo
import com.baylorw.spring_starter.model.GitHubUserRepoSummary
import com.baylorw.spring_starter.model.GitHubUserSummary

class TestData {
    static GitHubUser getUserAlice() {
        GitHubUser user = new GitHubUser()
        //--- Useful fields
        user.login = "Alice"
        user.avatarUrl = "https://avUrl"
        user.url = "https://url"
        user.name = "Alice Alisson"
        user.location = "Arkansas"
        user.email = "alice@aliceland.com"
        user.createdAt = "2013-01-30T03:58:15Z"
        //--- Not used fields
        user.bio = "Alice bio"
        user.company = "Alice Adventures"
        user.followers = 6
        user.userViewType = "public"
        return user
    }

    static List<GitHubUserRepo> getReposAlice() {
        List<GitHubUserRepo> repos = new ArrayList<>()
        for (i in 0..3) {
            GitHubUserRepo repo = new GitHubUserRepo()
            repos.add(repo)
            repo.name = "Alice Intelligence Demo " + i
            repo.htmlUrl = "https://github.com/alice/ai-demo-" + i
            repo.id = i
            repo.description = "repo " + i
        }
        return repos
    }

    static def GitHubUserSummary getUserSummaryAlice() {
        List<GitHubUserRepoSummary> repos = new ArrayList<>()
        for (i in 0..3) {
            GitHubUserRepoSummary repo = new GitHubUserRepoSummary("Alice Intelligence Demo " + i, "https://github.com/alice/ai-demo-" + i)
            repos.add(repo)
        }

        GitHubUserSummary user = GitHubUserSummary.builder()
                .userName("Alice")
                .avatar("https://avUrl")
                .url("https://url")
                .displayName("Alice Alisson")
                .geoLocation("Arkansas")
                .email("alice@aliceland.com")
                .createdAt("2013-01-30T03:58:15Z")
                .repos(repos)
                .build()
    }
}

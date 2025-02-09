package com.baylorw.branchtest.service;

import com.baylorw.branchtest.model.GitHubUser;
import com.baylorw.branchtest.model.GitHubUserRepo;
import com.baylorw.branchtest.model.GitHubUserRepoSummary;
import com.baylorw.branchtest.model.GitHubUserSummary;
import com.baylorw.branchtest.network.GitHubApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class GitHubService {
    private static Logger logger = LoggerFactory.getLogger(GitHubService.class);

    @Autowired
    private CacheManager cacheManager;

    private GitHubApi gitHubApi;

    public GitHubService(GitHubApi gitHubApi) {
        this.gitHubApi = gitHubApi;
    }


    public GitHubUserSummary getUserInfo(String userName, boolean shouldFlushCache) {
        logger.info("Request: getUserInfo user=" + userName);

        if (shouldFlushCache) {
            flushCaches();
        }

        //--- We need two calls but they can be done in parallel, so let's do that.
        //--- How? Spring has futures, executors, async methods, threads, flux, so many different ways.
        //--- i think this is the best way but, man, i don't know. Why does Spring have to have 8,000 ways to do everything?
        AtomicReference<GitHubUser> user = new AtomicReference<>();
        AtomicReference<List<GitHubUserRepo>> repos = new AtomicReference<>();
        CompletableFuture<GitHubUser> userCall = CompletableFuture.supplyAsync(() ->
                gitHubApi.fetchGitHubUser(userName)
        );
        CompletableFuture<List<GitHubUserRepo>> repoCall = CompletableFuture.supplyAsync(() ->
                gitHubApi.fetchGitHubUserRepos(userName)
        );
        userCall.thenAccept(result -> user.set(result));
        repoCall.thenAccept(result -> repos.set(result));
        CompletableFuture.allOf(userCall, repoCall).join();

        // TODO: Better error handling
        // Writing a friendly, comprehensive HTTP error handler is a lot of work for a short interview question.
        // To show the general concept, i wrote a global error handler for 404s (the most common error).
        // Other app-terminating errors would be handled similarly. See GlobalExceptionHandler for details.
        if (null == user.get()) {
            return null;
        }

        GitHubUserSummary summary = toSummary(user.get(), repos.get());

        return summary;
    }

    private GitHubUserSummary toSummary(GitHubUser user, List<GitHubUserRepo> repos) {
        List<GitHubUserRepoSummary> repoSummaries = new ArrayList<>();
        if (null != repos) {
            for (GitHubUserRepo repo : repos) {
                GitHubUserRepoSummary repoSummary = new GitHubUserRepoSummary(repo.name, repo.htmlUrl);
                repoSummaries.add(repoSummary);
            }
        }

        // TODO: There are automapping tools. But i forget their names (and bugs) so i'm doing this the boring way.
        // Using a lombok builder instead of Java records because records don't have named parameters, making them a pain to debug.
        GitHubUserSummary summary = GitHubUserSummary.builder()
                .userName(user.login)
                .avatar(user.avatarUrl)
                .url(user.url)
                .displayName(user.name)
                .geoLocation(user.location)
                .email(user.email)
                .createdAt(user.createdAt)
                .repos(repoSummaries)
                .build();
        return summary;
    }

    private void flushCaches() {
        //--- How do we know this works?
        //--- Check Actuator for cache size. Call flush and see that it drops down to 2 (the user cached after the flush).
        //---   http://localhost:8080/actuator/metrics/cache.size
        //-- i could also write a test but the instructions said not to spend TOO much time so i didn't. :)

        Collection<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (null != cache) {
                cache.clear();
            }
        }
    }
}

package com.baylorw.spring_starter.service

import com.baylorw.spring_starter.TestData
import com.baylorw.spring_starter.model.GitHubUser
import com.baylorw.spring_starter.model.GitHubUserRepo
import com.baylorw.spring_starter.model.GitHubUserSummary
import com.baylorw.spring_starter.network.GitHubApi
import org.springframework.cache.CacheManager
import spock.lang.Specification

class GitHubServiceSpec extends Specification {
    GitHubService service
    GitHubApi restCaller = Mock()
    CacheManager cacheManager = Mock()

    void setup() {
        service = new GitHubService(restCaller, cacheManager)

        cacheManager.getCacheNames() >> new ArrayList<String>()
    }


    def "GetUserInfo_merge and summarize test"() {
        given:
        restCaller.fetchGitHubUser(_) >> TestData.getUserAlice()
        restCaller.fetchGitHubUserRepos(_) >> TestData.getReposAlice()

        when:
        GitHubUserSummary actual = service.getUserInfo("alice", true)

        then:
        actual == TestData.getUserSummaryAlice()
    }

    def "toSummary_test"() {
        given:
        GitHubUser inputUser = TestData.getUserAlice()
        List<GitHubUserRepo> inputRepos = TestData.getReposAlice()

        when:
        GitHubUserSummary actual = service.toSummary(inputUser, inputRepos)

        then:
        actual != null
        actual.avatar == inputUser.avatarUrl
        actual.createdAt == inputUser.createdAt
        actual.displayName == inputUser.name
        actual.email == inputUser.email
        actual.geoLocation == inputUser.location
        actual.url == inputUser.url
        actual.userName == inputUser.login
        for (i in 0..(inputRepos.size() - 1)) {
            actual.repos.get(i).name() == inputRepos.get(i).name
            actual.repos.get(i).url() == inputRepos.get(i).htmlUrl
        }
    }
}

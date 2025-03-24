package com.baylorw.spring_starter.network;

import com.baylorw.spring_starter.model.GitHubUser;
import com.baylorw.spring_starter.model.GitHubUserRepo;
import com.baylorw.spring_starter.properties.GitHubProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
public class GitHubApi {
    private static Logger logger = LoggerFactory.getLogger(GitHubApi.class);

    private RestTemplate restTemplate;

    private GitHubProperties gitHubProperties;

    public GitHubApi(GitHubProperties gitHubProperties, AppRestTemplate restTemplate) {
        this.gitHubProperties = gitHubProperties;
        this.restTemplate = restTemplate;
    }

    @Cacheable("githubUser")
    public GitHubUser fetchGitHubUser(String userName) {
        String url = "";
        try {
            url = UriComponentsBuilder.fromHttpUrl(gitHubProperties.url())
                    .path(gitHubProperties.endpoints().user())
                    .buildAndExpand(userName)
                    .toUriString();
            logger.info("Fetching user data. userName={} url={}", userName, url);

            //--- This does nothing here (and is optional) but in more complex calls it's valuable.
            HttpEntity httpEntity = new HttpEntity<>(new HttpHeaders());

            ResponseEntity<GitHubUser> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, GitHubUser.class);
            if (responseEntity != null && responseEntity.getStatusCode() != null && responseEntity.getStatusCode().is2xxSuccessful()) {
                GitHubUser user = responseEntity.getBody();
                return user;
            } else {
                logger.error("Failed call to GitHub. userName={}, url={}", userName, url, responseEntity);
                return null;
            }
        } catch (HttpClientErrorException e) {
            //--- Our call was rejected (bad request, bad resource, no permission, etc.).
            if (404 == e.getRawStatusCode()) {
                logger.error("Error in call to GitHub. Invalid user. userName={}", userName);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist. userName=" + userName, e);
            }
            //--- Any 4xx error other than 404.
            logger.error("Error in call to GitHub. userName={}", userName, e);
            return null;
        } catch (ResourceAccessException e) {
            //--- 5xx. Couldn't connect to the server (bad server name, network issues, etc.).
            throw new ResourceAccessException("Unable to connect to " + url);
        } catch (Exception e) {
            logger.error("Error in call to GitHub. userName={}", userName, e);
            return null;
        }
    }

    @Cacheable("githubUserRepos")
    public List<GitHubUserRepo> fetchGitHubUserRepos(String userName) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(gitHubProperties.url())
                    .path(gitHubProperties.endpoints().repos())
                    .buildAndExpand(userName)
                    .toUriString();
            logger.info("Fetching repo data. url={} for userName={}", url, userName);

            HttpEntity httpEntity = new HttpEntity<>(new HttpHeaders());
            List<GitHubUserRepo> objectList1 = restTemplate.getForObject(url, List.class);
            ResponseEntity<List<GitHubUserRepo>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpEntity,
                    new ParameterizedTypeReference<List<GitHubUserRepo>>() {
                    }
            );
            if (responseEntity != null && responseEntity.getStatusCode() != null && responseEntity.getStatusCode().is2xxSuccessful()) {
                List<GitHubUserRepo> repos = responseEntity.getBody();
                return repos;
            } else {
                logger.error("Failed call to GitHub. userName={}, url={}", userName, url, responseEntity);
                return null;
            }
        } catch (HttpClientErrorException e) {
            if (404 == e.getRawStatusCode()) {
                //--- The user doesn't exist OR has no repos.
                //--- Let the actual user call throw an exception. If we're just missing repos, don't quit.
                return null;
            }
            //--- Any 4xx error other than 404.
            logger.error("Error in call to GitHub. userName={}", userName, e);
            return null;
        } catch (Exception ex) {
            logger.error("Error in call to GitHub. userName={}", userName, ex);
            return null;
        }
    }
}

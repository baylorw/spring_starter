package com.baylorw.branchtest.controller;

import com.baylorw.branchtest.model.GitHubUserSummary;
import com.baylorw.branchtest.service.GitHubService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController()
@RequestMapping("v1/github")
public class GitHubController {
    private static Logger logger = LoggerFactory.getLogger(GitHubController.class);

    private GitHubService gitHubService;

    //--- The recommended implicitly autowired constructor injection. But i kind of like the old annotated variables way.
    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping(value = {"/user/{userName}"})
    @Operation(summary = "Pull a user's GitHub user and repo info and return the best parts.")
    public ResponseEntity<?> handleUserAccountRequest(
            HttpServletRequest request,     // needed to check query params without values
            @PathVariable() String userName,
            @RequestParam Optional<Boolean> flush
    ) {
        //--- We cache responses and maybe the caller made a change and wants to see the latest. Let's let them force a cache flush.
        //--- If the caller passes ?flush, flush the cache. No value (not flush=true), just pass the parameter.
        //--- As a safety thing, if they do pass a parameter, respect it.
        boolean flushParameterPassed = request.getParameterMap().containsKey("flush");
        boolean explicitlySaidDontFlushCache = flush.isPresent() && (flush.get() == false);
        boolean shouldFlushCache = flushParameterPassed && !explicitlySaidDontFlushCache;

        GitHubUserSummary summary = gitHubService.getUserInfo(userName, shouldFlushCache);
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }
}

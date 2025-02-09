package com.baylorw.branchtest.controller

import com.baylorw.branchtest.TestData
import com.baylorw.branchtest.service.GitHubService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest
class GitHubControllerSpec extends Specification {

    @Autowired
    MockMvc mvc

    @SpringBean
    GitHubService service = Mock()

    def "HandleUserAccountRequest_captures_flush"() {
        given:
        expectedFlushCount * service.getUserInfo("octocat", true)
        (1 - expectedFlushCount) * service.getUserInfo("octocat", false)

        expect:
        mvc.perform(MockMvcRequestBuilders.get(url))

        where:
        name             | url                                   || expectedFlushCount
        "none"           | "/v1/github/user/octocat"             || 0
        "atomic"         | "/v1/github/user/octocat?flush"       || 1
        "explicit false" | "/v1/github/user/octocat?flush=false" || 0
        "explicit true"  | "/v1/github/user/octocat?flush=true"  || 1
    }

    def "HandleUserAccountRequest passes through service data"() {
        given:
        service.getUserInfo("alice", _) >> summary

        when:
        ResultActions response = mvc.perform(MockMvcRequestBuilders.get(url))
//                .andDo(MockMvcResultHandlers.print()) // Turn this on only while debugging, otherwise it's too much
                .andExpect(status().isOk())

        //--- There a couple of ways to test the result of a Controller call:
        //---   MockMvcResultMatchers.json()   - Test against a full JSON file (strictest test but breaks easily with system maintenance)
        //---   MockMvcResultMatchers.jsonPath - Look for elements in the JSON file
        //---   MvcResult response.andReturn() - Get the response body and convert to a strong typed object and check that
//        MvcResult result = response.andReturn()
//        GitHubUserSummary actual = new ObjectMapper().readValue(result.response.getContentAsString(), GitHubUserSummary.class);

        then:
        response.andExpect(status().isOk())
        response.andExpect(jsonPath('$.user_name').value(summary.userName))
        response.andExpect(jsonPath("avatar").value(summary.avatar))
        response.andExpect(jsonPath("url").value(summary.url))
        response.andExpect(jsonPath("geo_location").value(summary.geoLocation))
        response.andExpect(jsonPath("email").value(summary.email))
        response.andExpect(jsonPath("display_name").value(summary.displayName))
        response.andExpect(jsonPath("created_at").value(summary.createdAt))


        // TODO: Normally i would test multiple cases but this is just a job interview question, not prod, and this is boring :)
        where:
        name    | url                     || summary
        "alice" | "/v1/github/user/alice" || TestData.getUserSummaryAlice()
    }
}

package com.baylorw.spring_starter.controller;

import com.baylorw.spring_starter.util.TimeUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.ZonedDateTime;

@RestController
public class DefaultController {
    /**
     * If somebody just goes to the server address without a URL, return something other than a "page not found" error.
     * Goals:
     * - Because people make mistakes and accidentally go to the wrong servers at time, tell them which server we are.
     * - Because they might want to see if we're up, return the current time so they know they're not looking at a cached response.
     * - Because they might have forgotten the Swagger URL, give them a link to it.
     * - Because they might be debugging k8s uptime monitors, show the instance health data k8s is pinging.
     *
     * @return HTML
     */
    @GetMapping(value = {"/"})
    public ResponseEntity<?> handleBaseUrlRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String swaggerUrl = request.getRequestURL().toString() + "swagger-ui.html";
        String healthUrl = request.getRequestURL().toString() + "actuator/health";
        String cacheSizeUrl = request.getRequestURL().toString() + "actuator/metrics/cache.size";

        //--- A lot of debate on this vs StringBuilder. Apparently, the new(ish) advice is to use normal string cat.
        //--- For single line appends, the JVM compiles both this and SB to the same bytecode.
        String response =
                "<p>Example Service.</p>"
                        + "<p>current time: " + TimeUtil.format(ZonedDateTime.now(), "h:mm a z")
                        + " on " + TimeUtil.format(ZonedDateTime.now(), "E M-dd-yyyy") + "</p>"
                        + "<p>Swagger: <a href='" + swaggerUrl + "'>" + swaggerUrl + "</a></p>"
                        + "<p>Instance health: <a href='" + healthUrl + "'>" + healthUrl + "</a></p>"
                        + "<p>Cache size: <a href='" + cacheSizeUrl + "'>" + cacheSizeUrl + "</a></p>";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

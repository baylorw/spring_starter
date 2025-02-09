package com.baylorw.branchtest.network;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * AppRestTemplate is just a RestTemplate. Needed a custom class so that Spring can use it for Autowiring.
 * Which we want so that we can mock this when testing outgoing REST calls.
 */
@Component
public class AppRestTemplate extends RestTemplate {
}

package io.github.cepr0.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DemoClient extends ServiceAddressable {

    private final RestTemplate restTemplate;

    public DemoClient(Environment env, RestTemplate restTemplate) {
        super(env);
        this.restTemplate = restTemplate;
    }

    @Scheduled(initialDelay = 30_000, fixedRate = 10_000)
    public void callOpponentService() {
        String result;
        try {
            result = restTemplate.getForObject("/demo?id={ids}", String.class, Map.of("ids", List.of(1, 2).toArray()));
        } catch (Exception e) {
            log.error("[!] Error response: {}", e.getMessage());
            return;
        }
        log.info("[i] Client {} has got: {}", serviceAddress, result);
    }
}

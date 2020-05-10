package io.github.cepr0.demo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class Runner { //implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {
    private final String serviceName;
    private final String servicePort;
    private final RestTemplate restTemplate;
    private ApplicationContext applicationContext;

    public Runner(Environment env, RestTemplate restTemplate) {
        serviceName = env.getProperty("spring.application.name");
        servicePort = env.getProperty("server.port");
        this.restTemplate = restTemplate;
    }

    @SneakyThrows
    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void onReady(ApplicationReadyEvent event) {
        log.info("[i] Server {}:{} has started at {}", serviceName, servicePort, event.getTimestamp());

        Thread.sleep(10_000);
        while (true) {
            Thread.sleep(10_000);
            String result;
            try {
                result = restTemplate.getForObject("/demo?id={ids}", String.class, Map.of("ids", List.of(1, 2).toArray()));
            } catch (Exception e) {
                log.error("[!] Error response: {}", e.getMessage());
                continue;
            }
            log.info("[i] {}:{} has received: {}", serviceName, servicePort, result);
        }
    }

    // @Override
    // public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    //     this.applicationContext = applicationContext;
    // }
    //
    // @SneakyThrows
    // @Async
    // @Override
    // public void onApplicationEvent(ApplicationReadyEvent event) {
    //     if (event.getApplicationContext().equals(this.applicationContext)) {
    //         log.info("[i] Server {}:{} has started at {}", serviceName, servicePort, event.getTimestamp());
    //
    //         Thread.sleep(10_000);
    //         while (true) {
    //             String result = restTemplate.getForObject("/demo", String.class);
    //             log.info("[i] {}:{} has received: {}", serviceName, servicePort, result);
    //             Thread.sleep(3_000);
    //         }
    //     }
    // }
}

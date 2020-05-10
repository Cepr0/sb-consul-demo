package io.github.cepr0.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
@EnableScheduling
@EnableConfigurationProperties(AppProps.class)
@SpringBootApplication
public class Application {

    private final String serviceAddress;
    private final String opponentService;

    public Application(AppProps props, Environment env) {
        String serviceName = env.getProperty("spring.application.name");
        String servicePort = env.getProperty("server.port");
        this.serviceAddress = String.format("%s:%s", serviceName, servicePort);
        this.opponentService = props.getOpponentService();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady(ApplicationReadyEvent event) {
        log.info("[i] Server {} has started at {}", serviceAddress, event.getTimestamp());
    }

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder templateBuilder) {
        return templateBuilder
                .rootUri("http://" + opponentService)
                .errorHandler(new ResponseErrorHandler() {
                    @Override
                    public boolean hasError(ClientHttpResponse response) throws IOException {
                        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
                    }

                    @Override
                    public void handleError(ClientHttpResponse response) throws IOException {
                        log.error("[!] ResponseErrorHandler - error response: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
                    }
                })
                .build();
    }
}

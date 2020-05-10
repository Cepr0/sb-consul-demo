package io.github.cepr0.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Slf4j
@EnableAsync
@RestController
@RequestMapping("/demo")
@EnableConfigurationProperties(AppProps.class)
@SpringBootApplication
public class Application {

    private final String serviceName;
    private final String servicePort;
    private final String opponentService;

    @Value("${demo.route}") String route;

    @PostConstruct
    public void init() {
        log.info("[i] Route: {}", route);
    }

    public Application(Environment env, AppProps props) {
        serviceName = env.getProperty("spring.application.name");
        servicePort = env.getProperty("server.port");
        opponentService = props.getOpponentService();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
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
                        log.error("[!] Error response: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
                    }
                })
                .build();
    }

    @GetMapping
    public String get(@RequestParam(value = "id", required = false) List<Long> ids) {
        log.info("[i] Received ids: {}", ids);
        String n = String.format("%s:%s-%s", serviceName, servicePort, System.currentTimeMillis());
        log.info("[i] {}:{} has sent: {}", serviceName, servicePort, n);
        return n;
    }

    @GetMapping("${demo.route}")
    public Map<?, ?> get(WebRequest request) {
        log.info("[i] Received request to {}", route);

        var httpRequest = ((ServletWebRequest) request).getRequest();
        HttpHeaders headers = Collections
                .list(httpRequest.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        h -> Collections.list(httpRequest.getHeaders(h)),
                        (oldValue, newValue) -> newValue,
                        HttpHeaders::new
                ));

        String uri = MvcUriComponentsBuilder.fromMethodCall(on(Application.class).get(request))
                .build(Map.of("demo.route", route))
                .toString()
                .replace("$", "");
        return Map.of("value", route, "url", uri, "headers", headers);
    }
}

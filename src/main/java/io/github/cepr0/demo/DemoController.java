package io.github.cepr0.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Slf4j
@RestController
@RequestMapping("/demo")
public class DemoController extends ServiceAddressable {

    @Value("${demo.route}") String route;

    public DemoController(Environment env) {
        super(env);
    }

    @PostConstruct
    public void init() {
        log.info("[i] Route: {}", route);
    }

    @GetMapping
    public String get(@RequestParam(value = "id", required = false) List<Long> ids) {
        log.info("[i] Controller {} has received ids: {} in the request on '/demo' endpoint", serviceAddress, ids);
        String n = String.format("%s-%s", serviceAddress, System.currentTimeMillis());
        log.info("[i] Controller {} has sent back: {}", serviceAddress, n);
        return n;
    }

    @GetMapping("${demo.route}")
    public Map<?, ?> get(HttpServletRequest httpRequest) {
        log.info("[i] Controller {} has received the request to 'demo/{}' endpoint", serviceAddress, route);

        HttpHeaders headers = Collections
                .list(httpRequest.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        h -> Collections.list(httpRequest.getHeaders(h)),
                        (oldValue, newValue) -> newValue,
                        HttpHeaders::new
                ));

        String uri = MvcUriComponentsBuilder.fromMethodCall(on(DemoController.class).get(httpRequest))
                .build(Map.of("demo.route", route))
                .toString()
                .replace("$", "");
        return Map.of("value", route, "url", uri, "headers", headers);
    }
}

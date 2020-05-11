package io.github.cepr0.demo;

import org.springframework.core.env.Environment;

import static java.util.Optional.ofNullable;

public abstract class ServiceAddressable {

    protected final String serviceAddress;

    protected ServiceAddressable(Environment env) {
        String serviceName = env.getProperty("spring.application.name");
        String serverPort = env.getProperty("server.port");
        String cloudClientHostname = env.getProperty("spring.cloud.client.hostname");
        String serviceSuffix = ofNullable(serverPort).or(() -> ofNullable(cloudClientHostname))
                .orElseThrow(() -> new IllegalStateException("No server port nor client hostname values found!"));
        this.serviceAddress = String.format("%s:%s", serviceName, serviceSuffix);
    }
}

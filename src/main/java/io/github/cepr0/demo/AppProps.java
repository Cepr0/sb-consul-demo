package io.github.cepr0.demo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Validated
@ConfigurationProperties("demo")
public class AppProps {
    @NotEmpty private String opponentService;
    @NotEmpty private String route;
}

package com.elevatorsystem.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "elevator")
@Getter
@Setter
public class ElevatorConfig {
    private Integer timeout;
    private Integer maxFloor;
    private Integer minFloor;
}

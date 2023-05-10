package com.elevatorsystem.config;

import com.elevatorsystem.domain.ElevatorService;
import com.elevatorsystem.repositories.elevator.ElevatorJpaRepository;
import com.elevatorsystem.repositories.elevator.InMemoryElevatorRepository;
import com.elevatorsystem.repositories.requests.ElevatorRequestJpaRepository;
import com.elevatorsystem.repositories.requests.InMemoryElevatorRequestRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "inmemory")
    public ElevatorService inMemoryFarmService(InMemoryElevatorRepository elevatorRepository,
                                               InMemoryElevatorRequestRepository elevatorRequestRepository,
                                               ElevatorConfig elevatorConfig) {
        return new ElevatorService(elevatorRepository, elevatorRequestRepository, elevatorConfig);
    }

    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "jpa", matchIfMissing = true)
    public ElevatorService farmService(ElevatorJpaRepository elevatorRepository,
                                       ElevatorRequestJpaRepository elevatorRequestRepository,
                                       ElevatorConfig elevatorConfig) {
        return new ElevatorService(elevatorRepository, elevatorRequestRepository, elevatorConfig);
    }

}

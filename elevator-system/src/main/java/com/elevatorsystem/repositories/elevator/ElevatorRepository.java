package com.elevatorsystem.repositories.elevator;

import com.elevatorsystem.domain.Elevator.Elevator;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ElevatorRepository {
    Optional<Elevator> findById(UUID uuid);

    Elevator save(Elevator elevator);

    List<Elevator> findAll();
}

package com.elevatorsystem.repositories.elevator;

import com.elevatorsystem.domain.Elevator.Elevator;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryElevatorRepository implements ElevatorRepository {

    private final List<Elevator> elevatorList = new ArrayList<>();

    @Override
    public Optional<Elevator> findById(UUID id) {
        return elevatorList.stream().filter(elevator -> elevator.getId().equals(id)).findFirst();
    }

    @Override
    public Elevator save(Elevator elevator) {
        elevatorList.add(elevator);
        return elevator;
    }

    @Override
    public List<Elevator> findAll() {
        return elevatorList;
    }
}

package com.elevatorsystem.repositories.requests;

import com.elevatorsystem.domain.Request.ElevatorRequest;
import com.elevatorsystem.repositories.elevator.InMemoryElevatorRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class InMemoryElevatorRequestRepository implements ElevatorRequestRepository{

    private final List<ElevatorRequest> elevatorRequestList = new ArrayList<>();

    private final InMemoryElevatorRepository inMemoryElevatorRepository;

    public InMemoryElevatorRequestRepository(InMemoryElevatorRepository inMemoryElevatorRepository) {
        this.inMemoryElevatorRepository = inMemoryElevatorRepository;
    }

    @Override
    public List<ElevatorRequest> findByElevatorId(UUID id) {
        return elevatorRequestList.stream().filter(elevatorRequest -> elevatorRequest.getElevator().getId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public ElevatorRequest save(ElevatorRequest elevatorRequest) {
        elevatorRequestList.add(elevatorRequest);
        var elevator = elevatorRequest.getElevator();
        elevator.getRequests().add(elevatorRequest);
        inMemoryElevatorRepository.save(elevator);
        return elevatorRequest;
    }
}

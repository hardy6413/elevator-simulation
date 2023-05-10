package com.elevatorsystem.repositories.requests;

import com.elevatorsystem.domain.Elevator.Elevator;
import com.elevatorsystem.domain.Request.ElevatorRequest;

import java.util.List;
import java.util.UUID;

public interface ElevatorRequestRepository {
    List<ElevatorRequest> findByElevatorId(UUID id);

    ElevatorRequest save(ElevatorRequest elevatorRequest);
}

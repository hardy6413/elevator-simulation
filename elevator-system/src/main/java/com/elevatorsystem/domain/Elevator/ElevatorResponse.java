package com.elevatorsystem.domain.Elevator;

import com.elevatorsystem.domain.Request.ElevatorRequest;

import java.util.List;

public record ElevatorResponse(String id, Integer currentFloor, String status, List<ElevatorRequest> requests) {

    public static ElevatorResponse fromElevator(Elevator elevator) {
        return new ElevatorResponse(elevator.getId().toString(), elevator.getCurrentFloor(), elevator.getElevatorStatus().toString(), elevator.getRequests());
    }

}

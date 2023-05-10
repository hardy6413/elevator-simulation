package com.elevatorsystem.domain.Request;

import java.util.UUID;

public record ElevatorRequestDto(UUID elevatorId, Integer floor) {
}

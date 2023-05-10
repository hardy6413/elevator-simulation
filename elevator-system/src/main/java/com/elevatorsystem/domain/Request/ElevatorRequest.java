package com.elevatorsystem.domain.Request;

import com.elevatorsystem.domain.Elevator.Elevator;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ElevatorRequest {

    @Id
    private UUID id;
    private RequestStatus requestStatus;
    private Integer floor;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elevator_id", nullable = false)
    private Elevator elevator;
    private Instant createdAt;

    public ElevatorRequest() {

    }

    public ElevatorRequest(Integer floor, Elevator elevator) {
        this.id = UUID.randomUUID();
        this.requestStatus = RequestStatus.PENDING;
        this.floor = floor;
        this.elevator = elevator;
        this.createdAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElevatorRequest that = (ElevatorRequest) o;
        return Objects.equals(id, that.id) && requestStatus == that.requestStatus && Objects.equals(floor, that.floor) && Objects.equals(elevator, that.elevator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, requestStatus, floor, elevator);
    }
}

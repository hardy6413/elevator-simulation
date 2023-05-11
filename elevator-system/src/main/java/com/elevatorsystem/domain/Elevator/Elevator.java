package com.elevatorsystem.domain.Elevator;

import com.elevatorsystem.domain.Request.ElevatorRequest;
import com.elevatorsystem.domain.Request.RequestStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Elevator {
    @Id
    @Column(name = "elevator_id")
    private UUID id;
    private Integer currentFloor;
    @JsonManagedReference
    @OneToMany(mappedBy = "elevator", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("createdAt ASC")
    private List<ElevatorRequest> requests = new ArrayList<>();
    private ElevatorStatus elevatorStatus;

    public Elevator(Integer currentFloor) {
        this.id = UUID.randomUUID();
        this.currentFloor = currentFloor;
        this.elevatorStatus = ElevatorStatus.WAITING;
    }

    public Elevator() {

    }

    public void moveToNextFloor() {
        if (this.getElevatorStatus().equals(ElevatorStatus.DOWNWARD)) {
            this.setCurrentFloor(this.currentFloor - 1);
        } else {
            this.setCurrentFloor(this.currentFloor + 1);
        }
    }

    public boolean hasSimilarRequest(Integer floor) {

        return this.getRequests()
                .stream()
                .filter(elevatorRequest -> elevatorRequest.getRequestStatus().equals(RequestStatus.PENDING))
                .anyMatch(elevatorRequest -> elevatorRequest.getFloor().equals(floor));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Elevator elevator = (Elevator) o;
        return Objects.equals(id, elevator.id) && Objects.equals(currentFloor, elevator.currentFloor) && Objects.equals(requests, elevator.requests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currentFloor, requests);
    }
}

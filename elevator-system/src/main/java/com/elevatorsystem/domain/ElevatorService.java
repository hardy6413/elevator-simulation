package com.elevatorsystem.domain;

import com.elevatorsystem.config.ElevatorConfig;
import com.elevatorsystem.domain.Elevator.Elevator;
import com.elevatorsystem.domain.Request.ElevatorRequest;
import com.elevatorsystem.domain.Request.ElevatorRequestDto;
import com.elevatorsystem.domain.Request.RequestStatus;
import com.elevatorsystem.repositories.elevator.ElevatorRepository;
import com.elevatorsystem.repositories.requests.ElevatorRequestRepository;
import com.elevatorsystem.utils.ElevatorNotFoundException;
import com.elevatorsystem.domain.Elevator.ElevatorStatus;
import com.elevatorsystem.utils.RequestedFloorOutOfBoundException;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ElevatorService {
    private final ElevatorRepository elevatorRepository;
    private final ElevatorRequestRepository elevatorRequestRepository;
    private final ElevatorConfig elevatorConfig;

    public ElevatorService(ElevatorRepository elevatorRepository, ElevatorRequestRepository elevatorRequestRepository, ElevatorConfig elevatorConfig) {
        this.elevatorRepository = elevatorRepository;
        this.elevatorRequestRepository = elevatorRequestRepository;
        this.elevatorConfig = elevatorConfig;
    }

    public List<Elevator> findAll() {
        return this.elevatorRepository.findAll();
    }


    public void runOne(UUID id) {
        var elevator = elevatorRepository.findById(id).orElseThrow(() -> new ElevatorNotFoundException("Elevator for id " + id + " not found"));
        if (elevator.getElevatorStatus().equals(ElevatorStatus.WAITING)) {
            var floorsToReach = elevator.getRequests()
                    .stream().filter(request -> request.getRequestStatus().equals(RequestStatus.PENDING))
                    .collect(Collectors.toList());

            while (floorsToReach.size() > 0) {
                var currentFloor = elevator.getCurrentFloor();
                var request = getNearestFloorRequest(currentFloor, floorsToReach, elevator.getElevatorStatus());

                chooseElevatorDirection(elevator, currentFloor, request); // chose direction and close doors

                var prevStatus = elevator.getElevatorStatus(); // remember direction

                simulateElevatorMove(elevator); //move elevator
                openDoorsIfOnDesiredFloor(elevator, request); //open doors

                if (floorsToReach.size() > 1) { // close doors and if any request was added in the same direction during moving finish it first(the closes of them)
                    elevator.setElevatorStatus(prevStatus);
                    elevatorRepository.save(elevator);
                    simulateDelay();
                }
                //check for new requests
                floorsToReach = elevatorRequestRepository.findByElevatorId(elevator.getId())
                        .stream()
                        .filter(elevatorRequest -> elevatorRequest.getRequestStatus().equals(RequestStatus.PENDING))
                        .sorted(Comparator.comparing(ElevatorRequest::getCreatedAt))
                        .collect(Collectors.toList());
            }
            elevator.setElevatorStatus(ElevatorStatus.WAITING);
            elevatorRepository.save(elevator);
            simulateDelay();
        }
    }


    public void addRequest(ElevatorRequestDto elevatorRequestDto) {
        if (elevatorRequestDto.floor() < this.elevatorConfig.getMinFloor() || elevatorRequestDto.floor() > this.elevatorConfig.getMaxFloor()){
            throw new RequestedFloorOutOfBoundException("Floor is not in range");
        }

        var elevator = elevatorRepository.findById(elevatorRequestDto.elevatorId())
                .orElseThrow(() -> new ElevatorNotFoundException("Elevator for id " + elevatorRequestDto.elevatorId() + " not found"));

        if (!elevator.hasSimilarRequest(elevatorRequestDto.floor()) && !elevator.getCurrentFloor().equals(elevatorRequestDto.floor())) {
            var request = new ElevatorRequest(elevatorRequestDto.floor(), elevator);
            elevatorRequestRepository.save(request);
        }
    }

    public Elevator createElevator() {
        var elevator = new Elevator(0);
        return elevatorRepository.save(elevator);
    }

    public Elevator findElevatorById(UUID id) {
        return elevatorRepository.findById(id).orElseThrow(() -> new ElevatorNotFoundException("Elevator for id " + id + " not found"));
    }

    public ElevatorRequest getNearestFloorRequest(Integer currentFloor, List<ElevatorRequest> requests, ElevatorStatus status) {
        var nearestFloorRequest = requests.get(0); //in case of direction change
        var firstFloor = nearestFloorRequest.getFloor();
        if (status.equals(ElevatorStatus.UPWARD)) {
            return getUpwardRequests(requests, currentFloor, nearestFloorRequest);
        } else if (status.equals(ElevatorStatus.DOWNWARD)) {
            return getDownwardRequests(requests, currentFloor, nearestFloorRequest);
        } else if (status.equals(ElevatorStatus.WAITING)) {
            if (firstFloor > currentFloor) {
                return getUpwardRequests(requests, currentFloor, nearestFloorRequest);
            } else {
                return getDownwardRequests(requests, currentFloor, nearestFloorRequest);
            }
        }
        return nearestFloorRequest;
    }

    private ElevatorRequest getUpwardRequests(List<ElevatorRequest> requests, Integer currentFloor, ElevatorRequest nearestFloorRequest) {
        var upwardRequests = requests.stream().filter(request -> request.getFloor() > currentFloor).toList();
        return drawRequest(currentFloor, upwardRequests, nearestFloorRequest);
    }

    private ElevatorRequest getDownwardRequests(List<ElevatorRequest> requests, Integer currentFloor, ElevatorRequest nearestFloorRequest) {
        var downwardRequests = requests.stream().filter(request -> request.getFloor() < currentFloor).toList();
        return drawRequest(currentFloor, downwardRequests, nearestFloorRequest);
    }

    private void chooseElevatorDirection(Elevator elevator, Integer currentFloor, ElevatorRequest request) {
        if (request.getFloor() > currentFloor) {
            elevator.setElevatorStatus(ElevatorStatus.UPWARD);
        } else {
            elevator.setElevatorStatus(ElevatorStatus.DOWNWARD);
        }
        elevatorRepository.save(elevator);
        simulateDelay(); //close doors
    }

    private void openDoorsIfOnDesiredFloor(Elevator elevator, ElevatorRequest request) {
        if (Objects.equals(elevator.getCurrentFloor(), request.getFloor())) { // open doors
            request.setRequestStatus(RequestStatus.COMPLETED);
            elevatorRequestRepository.save(request);
            elevator.setElevatorStatus(ElevatorStatus.OPENED);
            elevatorRepository.save(elevator);
            simulateDelay();
        }
    }

    private void simulateElevatorMove(Elevator elevator) {
        elevator.moveToNextFloor();
        elevatorRepository.save(elevator);
        simulateDelay();
        System.out.println("I am on the floor" + elevator.getCurrentFloor() + " elevator :" + elevator.getId());
    }

    private ElevatorRequest drawRequest(Integer currentFloor, List<ElevatorRequest> requests, ElevatorRequest nearestFloorRequest) {
        var nearestFloorDistance = Integer.MAX_VALUE;
        for (ElevatorRequest req : requests) {
            var distance = Math.abs(currentFloor - req.getFloor());
            if (distance == 1) {
                return req;
            }
            if (nearestFloorDistance > distance) {
                nearestFloorDistance = distance;
                nearestFloorRequest = req;
            }
        }
        return nearestFloorRequest;
    }

    private void simulateDelay() {
        try {
            TimeUnit.SECONDS.sleep(elevatorConfig.getTimeout());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

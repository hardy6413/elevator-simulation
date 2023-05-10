package com.elevatorsystem.web;

import com.elevatorsystem.domain.Request.ElevatorRequestDto;
import com.elevatorsystem.domain.Elevator.ElevatorResponse;
import com.elevatorsystem.domain.ElevatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
public class ElevatorController {

    private final ElevatorService elevatorService;

    public ElevatorController(ElevatorService elevatorService) {
        this.elevatorService = elevatorService;
    }


    @PostMapping("/elevator")
    public ResponseEntity<?> createElevator() {
        var elevator = elevatorService.createElevator();
        return ResponseEntity.ok(elevator);
    }

    @PostMapping("/elevator/request")
    public ResponseEntity<?> createRequest(@RequestBody ElevatorRequestDto ElevatorRequestDto) {
        elevatorService.addRequest(ElevatorRequestDto);
        return ResponseEntity.ok("request added");
    }

    @PutMapping("/elevator/{id}/run")
    public ResponseEntity<?> run(@PathVariable UUID id) {
        elevatorService.runOne(id);
        return ResponseEntity.ok("run");
    }


    @GetMapping("/elevator")
    public ResponseEntity<?> getElevators() {
        var elevators = elevatorService.findAll().stream().map(ElevatorResponse::fromElevator).toList();
        return ResponseEntity.ok(elevators);
    }

}

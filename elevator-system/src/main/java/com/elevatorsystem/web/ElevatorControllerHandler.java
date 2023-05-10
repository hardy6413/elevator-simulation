package com.elevatorsystem.web;

import com.elevatorsystem.utils.ElevatorNotFoundException;
import com.elevatorsystem.utils.RequestedFloorOutOfBoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ElevatorControllerHandler {

    @ExceptionHandler(ElevatorNotFoundException.class)
    public ResponseEntity<String> handleElevatorNotFound(Exception exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RequestedFloorOutOfBoundException.class)
    public ResponseEntity<String> handleFloorOutOfBound(Exception exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

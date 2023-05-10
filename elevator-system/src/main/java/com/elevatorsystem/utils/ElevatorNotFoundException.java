package com.elevatorsystem.utils;

public class ElevatorNotFoundException extends RuntimeException {
    public ElevatorNotFoundException(String message) {
        super(message);
    }
}

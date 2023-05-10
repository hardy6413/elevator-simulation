package com.elevatorsystem.utils;

public class RequestedFloorOutOfBoundException extends RuntimeException{
    public RequestedFloorOutOfBoundException(String message) {
        super(message);
    }
}

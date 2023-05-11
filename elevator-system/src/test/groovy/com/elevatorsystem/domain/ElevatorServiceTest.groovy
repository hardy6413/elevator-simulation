package com.elevatorsystem.domain

import com.elevatorsystem.config.ElevatorConfig
import com.elevatorsystem.domain.Elevator.Elevator
import com.elevatorsystem.domain.Elevator.ElevatorStatus
import com.elevatorsystem.domain.Request.ElevatorRequest
import com.elevatorsystem.domain.Request.ElevatorRequestDto
import com.elevatorsystem.domain.Request.RequestStatus
import com.elevatorsystem.repositories.elevator.ElevatorRepository
import com.elevatorsystem.repositories.elevator.InMemoryElevatorRepository
import com.elevatorsystem.repositories.requests.ElevatorRequestRepository
import com.elevatorsystem.repositories.requests.InMemoryElevatorRequestRepository
import com.elevatorsystem.utils.ElevatorNotFoundException
import com.elevatorsystem.utils.RequestedFloorOutOfBoundException
import spock.lang.Specification
import spock.lang.Unroll

class ElevatorServiceTest extends Specification {

    ElevatorService elevatorService
    ElevatorRepository elevatorRepository
    ElevatorRequestRepository elevatorRequestRepository
    ElevatorConfig elevatorConfig

    def setup() {
        elevatorConfig = new ElevatorConfig()
        elevatorConfig.setTimeout(0)
        elevatorConfig.setMinFloor(0)
        elevatorConfig.setMaxFloor(16)
        elevatorRepository = new InMemoryElevatorRepository()
        elevatorRequestRepository = new InMemoryElevatorRequestRepository(elevatorRepository)
        elevatorService = new ElevatorService(elevatorRepository, elevatorRequestRepository, elevatorConfig)
    }

    def "should find all elevators"() {
        given: "elevators"
        def firstElevator = elevatorService.createElevator()
        def secondElevator = elevatorService.createElevator()

        when: "executing find all elevators"
        def elevatorsList = elevatorService.findAll()

        then: "every elevator is found"
        assert elevatorsList.size() == 2
        elevatorsList.every {
            it.getCurrentFloor() == 0
            it.getElevatorStatus() == ElevatorStatus.WAITING
            (it.getId() == firstElevator.getId() || it.getId() == secondElevator.getId())
        }
    }

    def "should run if the elevator is nonexistent"() {
        given: "nonexistent elevator id"
        def nonExistentElevatorId = UUID.randomUUID()

        when: "running the elevator"
        elevatorService.runOne(nonExistentElevatorId)

        then: "elevator is waiting on desired floor"
        thrown(ElevatorNotFoundException.class)
    }

    def "should run to the given floor and wait there"() {
        given: "elevator"
        def elevator = elevatorService.createElevator()

        and: "elevator request"
        def request = new ElevatorRequestDto(elevator.getId(), 5)
        elevatorService.addRequest(request)

        when: "running the elevator"
        elevatorService.runOne(elevator.getId())

        then: "elevator is waiting on desired floor"
        def foundElevator = elevatorService.findElevatorById(elevator.getId())

        assert foundElevator.getCurrentFloor() == 5
        assert foundElevator.getElevatorStatus() == ElevatorStatus.WAITING
        assert foundElevator.getRequests()[0].getRequestStatus() == RequestStatus.COMPLETED
    }

    def "should run to the further floor at last despite creation time and wait there"() {
        given: "elevator"
        def elevator = elevatorService.createElevator()

        and: "elevator requests"
        def furtherFloor = 10
        def closerFloor = 5
        def furtherFloorRequest = new ElevatorRequestDto(elevator.getId(), furtherFloor)
        elevatorService.addRequest(furtherFloorRequest)
        def closerFloorRequest = new ElevatorRequestDto(elevator.getId(), closerFloor)
        elevatorService.addRequest(closerFloorRequest)

        when: "running the elevator"
        elevatorService.runOne(elevator.getId())

        then: "elevator is waiting on desired floor"
        def foundElevator = elevatorService.findElevatorById(elevator.getId())

        assert foundElevator.getId() == elevator.getId()
        assert foundElevator.getCurrentFloor() == 10
        assert foundElevator.getElevatorStatus() == ElevatorStatus.WAITING
        foundElevator.getRequests().every {
            it.getRequestStatus() == RequestStatus.COMPLETED
            it.getElevator().id == foundElevator.id
            (it.getFloor() == furtherFloor || it.getFloor() == closerFloor)
        }
    }

    def "should throw error when trying to create request for nonexistent elevator"() {
        given: "elevator request"
        def nonExistentId = UUID.randomUUID()
        def desiredFloor = 1
        def request = new ElevatorRequestDto(nonExistentId, desiredFloor)

        when: "executing add request"
        elevatorService.addRequest(request)

        then: "request is found"
        thrown(ElevatorNotFoundException.class)
    }

    def "should add request and then find it"() {
        given: "elevator"
        def elevator = elevatorService.createElevator()

        and: "elevator request"
        def desiredFloor = 1
        def request = new ElevatorRequestDto(elevator.getId(), desiredFloor)

        when: "executing add request"
        elevatorService.addRequest(request)

        then: "request is found"
        var foundRequest = elevatorService.findElevatorById(elevator.getId()).getRequests()[0]
        assert foundRequest.getFloor() == desiredFloor
        assert foundRequest.getRequestStatus() == RequestStatus.PENDING
        assert foundRequest.getElevator().id == elevator.getId()
    }

    def "shouldn't add another request when floor is waiting on requested floor"() {
        given: "elevator"
        def elevator = elevatorService.createElevator()

        and: "elevator request"
        def desiredFloor = 0
        def request = new ElevatorRequestDto(elevator.getId(), desiredFloor)

        when: "executing add request"
        elevatorService.addRequest(request)

        then: "request is found"
        def requests = elevatorService.findElevatorById(elevator.getId()).getRequests()
        requests.size() == 0
    }

    @Unroll
    def "shouldn't add another request when floor is not in range"() {
        given: "elevator"
        def elevator = elevatorService.createElevator()

        and: "elevator request with out of boundaries floor"
        def request = new ElevatorRequestDto(elevator.getId(), desiredFloor)

        when: "executing add request"
        elevatorService.addRequest(request)

        then: "error is thrown"
        thrown(RequestedFloorOutOfBoundException.class)

        where:
        desiredFloor << [-100, 200]
    }

    def "shouldn't add another pending request with the same floor selected again"() {
        given: "elevator"
        def elevator = elevatorService.createElevator()

        and: "elevator request"
        def desiredFloor = 1
        def request = new ElevatorRequestDto(elevator.getId(), desiredFloor)

        when: "executing add request"
        elevatorService.addRequest(request)

        then: "request is found"
        var foundRequest = elevatorService.findElevatorById(elevator.getId()).getRequests()[0]

        and: "properly saved"
        assert foundRequest.getFloor() == desiredFloor
        assert foundRequest.getRequestStatus() == RequestStatus.PENDING
        assert foundRequest.getElevator().id == elevator.getId()

        when: "trying to add request with same floor"
        def sameFloorRequest = new ElevatorRequestDto(elevator.getId(), desiredFloor)
        elevatorService.addRequest(sameFloorRequest)

        then: "another request wasn't added"
        elevatorService.findElevatorById(elevator.getId()).getRequests().size() == 1
    }

    def "should throw error when trying to crete request for non existing elevator"() {
        given: "elevator"
        def elevator = elevatorService.createElevator()

        and: "elevator request"
        def desiredFloor = 1
        def request = new ElevatorRequestDto(elevator.getId(), desiredFloor)

        when: "executing add request"
        elevatorService.addRequest(request)

        then: "request is found"
        var foundRequest = elevatorService.findElevatorById(elevator.getId()).getRequests()[0]

        and: "properly saved"
        assert foundRequest.getFloor() == desiredFloor
        assert foundRequest.getRequestStatus() == RequestStatus.PENDING
        assert foundRequest.getElevator().id == elevator.getId()

        when: "trying to add request with same floor"
        def sameFloorRequest = new ElevatorRequestDto(elevator.getId(), desiredFloor)
        elevatorService.addRequest(sameFloorRequest)

        then: "another request wasn't added"
        elevatorService.findElevatorById(elevator.getId()).getRequests().size() == 1
    }

    def "should create elevator"() {
        when: "creating elevator"
        def elevator = elevatorService.createElevator()
        then: "elevator is created"
        def createdElevator = elevatorService.findAll()[0]
        assert createdElevator == elevator
    }

    @Unroll
    def "should get nearest floor or first submitted floor if the elevator was waiting"() {
        expect: "nearest floor is always found"
        elevatorService.getNearestFloorRequest(currentFloor, requests, elevatorStatus).getFloor() == desiredFloor

        where:
        currentFloor | requests                                         | elevatorStatus            | desiredFloor
        3            | [getRequest(10), getRequest(2)]                  | ElevatorStatus.UPWARD     | 10
        3            | [getRequest(8), getRequest(10)]                  | ElevatorStatus.UPWARD     | 8
        3            | [getRequest(10), getRequest(2)]                  | ElevatorStatus.DOWNWARD   | 2
        3            | [getRequest(1), getRequest(2)]                   | ElevatorStatus.DOWNWARD   | 2
        3            | [getRequest(8), getRequest(1), getRequest(10)]   | ElevatorStatus.DOWNWARD   | 1
        3            | [getRequest(2), getRequest(4)]                   | ElevatorStatus.WAITING    | 2
        0            | [getRequest(5), getRequest(2)]                   | ElevatorStatus.WAITING    | 2
        3            | [getRequest(5), getRequest(2), getRequest(4)]    | ElevatorStatus.WAITING    | 4
        3            | [getRequest(1), getRequest(4), getRequest(2)]    | ElevatorStatus.WAITING    | 2
        3            | [getRequest(2), getRequest(4), getRequest(1)]    | ElevatorStatus.WAITING    | 2

    }

    private getRequest(Integer floor){
        return new ElevatorRequest(floor, new Elevator())
    }
}

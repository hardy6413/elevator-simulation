package com.elevatorsystem.web

import com.elevatorsystem.ElevatorSystemApplication
import com.elevatorsystem.domain.Elevator.Elevator
import com.elevatorsystem.domain.Elevator.ElevatorStatus
import com.elevatorsystem.domain.Elevator.ElevatorResponse
import com.elevatorsystem.domain.Request.ElevatorRequestDto
import com.elevatorsystem.repositories.elevator.ElevatorJpaRepository
import com.elevatorsystem.repositories.requests.ElevatorRequestJpaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT

@SpringBootTest(webEnvironment = DEFINED_PORT)
@ContextConfiguration(classes = ElevatorSystemApplication.class)
@ActiveProfiles("test")
class ElevatorControllerTest extends Specification {

    ElevatorController elevatorController
    @Autowired
    ElevatorRequestJpaRepository elevatorRequestJpaRepository
    @Autowired
    ElevatorJpaRepository elevatorJpaRepository
    TestRestTemplate restTemplate
    String ELEVATOR_URL = 'http://localhost:8080/elevator'
    String ELEVATOR_RUN_URL = 'http://localhost:8080/elevator/{id}/run'
    String REQUEST_URL = 'http://localhost:8080/elevator/request'

    void setup() {
        restTemplate = new TestRestTemplate()
        elevatorRequestJpaRepository.deleteAll()
        elevatorJpaRepository.deleteAll()
    }

    def "should create and find elevator"() {
        when: "executing request to create elevator"
        def response = restTemplate.postForEntity(ELEVATOR_URL, null, Elevator.class)

        then: "ok status is returned"
        response.statusCode == HttpStatus.OK

        and: "elevator is found"
        def elevator = restTemplate.getForEntity(ELEVATOR_URL, ElevatorResponse[].class).getBody()[0]
        elevator.status() == ElevatorStatus.WAITING.toString()
        elevator.currentFloor() == 0
        elevator.id() == response.getBody().getId().toString()
        elevator.requests().size() == 0
    }

    def "should create elevator request and then find it"() {
        given: "elevator and elevator request"
        def createdElevator = restTemplate.postForEntity(ELEVATOR_URL, null, Elevator.class).getBody()
        def desiredFloor = 3
        def elevatorRequest = new ElevatorRequestDto(createdElevator.id, desiredFloor)

        when: "executing create elevator request"
        def response = restTemplate.postForEntity(REQUEST_URL, elevatorRequest, String.class)

        then: "request was created and assigned to elevator"
        def elevator = restTemplate.getForEntity(ELEVATOR_URL, ElevatorResponse[].class).getBody()[0]
        response.getStatusCode() == HttpStatus.OK
        elevator.requests().size() == 1
        def createdRequest = elevator.requests()[0]
        assert createdRequest.getFloor() == elevatorRequest.floor()
    }

    def "should not  create another elevator request for the same pending floor"() {
        given: "elevator and elevator request"
        def createdElevator = restTemplate.postForEntity(ELEVATOR_URL, null, Elevator.class).getBody()
        def desiredFloor = 3
        def elevatorRequest = new ElevatorRequestDto(createdElevator.id, desiredFloor)

        when: "executing create elevator request"
        def response = restTemplate.postForEntity(REQUEST_URL, elevatorRequest, String.class)

        and: "again executing request for the same pending floor"
        restTemplate.postForEntity(REQUEST_URL, elevatorRequest, String.class)

        then: "only one request was created and assigned to elevator"
        def elevator = restTemplate.getForEntity(ELEVATOR_URL, ElevatorResponse[].class).getBody()[0]
        response.getStatusCode() == HttpStatus.OK
        elevator.requests().size() == 1
        def createdRequest = elevator.requests()[0]
        assert createdRequest.getFloor() == elevatorRequest.floor()
    }

    def "should throw error when trying to create request for nonexistent elevator"() {
        given: "elevator request"
        def nonexistentElevatorId = UUID.randomUUID()
        def desiredFloor = 3
        def elevatorRequest = new ElevatorRequestDto(nonexistentElevatorId, desiredFloor)

        when: "executing create elevator request for nonexistent elevator"
        def response = restTemplate.postForEntity(REQUEST_URL, elevatorRequest, String.class)

        then: "error message was returned"
        response.getStatusCode() == HttpStatus.NOT_FOUND
        response.getBody() == "Elevator for id " + nonexistentElevatorId + " not found"
    }

    def "should throw error when trying to create request for floor out of boundaries"() {
        given: "elevator request with out of bounds floor"
        def nonexistentElevatorId = UUID.randomUUID()
        def elevatorRequest = new ElevatorRequestDto(nonexistentElevatorId, desiredFloor)

        when: "executing create elevator request for nonexistent elevator"
        def response = restTemplate.postForEntity(REQUEST_URL, elevatorRequest, String.class)

        then: "error message was returned"
        response.getStatusCode() == HttpStatus.BAD_REQUEST
        response.getBody() == "Floor is not in range"

        where:
        desiredFloor << [-100, 200]
    }

    def "should run to desired floor and wait there"() {
        given: "elevator and elevator request"
        def createdElevator = restTemplate.postForEntity(ELEVATOR_URL, null, Elevator.class).getBody()
        def desiredFloor = 3
        def elevatorRequest = new ElevatorRequestDto(createdElevator.id, desiredFloor)
        restTemplate.postForEntity(REQUEST_URL, elevatorRequest, String.class)

        when: "executing elevator run"
        restTemplate.put(ELEVATOR_RUN_URL, null, createdElevator.getId())

        then: "elevator is waiting on desired floor"
        def elevator = restTemplate.getForEntity(ELEVATOR_URL, ElevatorResponse[].class).getBody()[0]
        elevator.status() == ElevatorStatus.WAITING.toString()
        elevator.currentFloor() == desiredFloor
        elevator.id() == createdElevator.getId().toString()
        elevator.requests().size() == 1
        elevator.requests()[0].getFloor() == desiredFloor
    }

    def "should throw error when trying run nonexistent elevator"() {
        given: "nonexistent elevator id"
        def nonexistentElevatorId = UUID.randomUUID()

        when: "executing elevator run"
        var response = restTemplate.exchange(ELEVATOR_RUN_URL, HttpMethod.PUT, null, String.class, nonexistentElevatorId)

        then: "error message was returned"
        response.getStatusCode() == HttpStatus.NOT_FOUND
        response.getBody() == "Elevator for id " + nonexistentElevatorId + " not found"
    }

    def "should run to the furthest floor at last and wait there"() {
        given: "elevator and elevator requests"
        def createdElevator = restTemplate.postForEntity(ELEVATOR_URL, null, Elevator.class).getBody()
        def furtherFloor = 10
        def closerFloor = 5
        def furtherFloorRequest = new ElevatorRequestDto(createdElevator.id, furtherFloor)
        def closerFloorRequest = new ElevatorRequestDto(createdElevator.id, closerFloor)
        restTemplate.postForEntity(REQUEST_URL, furtherFloorRequest, String.class)
        restTemplate.postForEntity(REQUEST_URL, closerFloorRequest, String.class)

        when: "executing elevator run"
        restTemplate.put(ELEVATOR_RUN_URL, null, createdElevator.getId())

        then: "elevator is waiting on the furthest floor despite submitted time"
        def elevator = restTemplate.getForEntity(ELEVATOR_URL, ElevatorResponse[].class).getBody()[0]
        elevator.status() == ElevatorStatus.WAITING.toString()
        elevator.currentFloor() == furtherFloor
        elevator.id() == createdElevator.getId().toString()
        elevator.requests().size() == 2
        def floors = elevator.requests().stream().map(r -> r.getFloor()).toList()
        floors.contains(furtherFloor)
        floors.contains(closerFloor)
    }

}

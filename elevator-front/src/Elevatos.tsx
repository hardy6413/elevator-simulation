import CSS from 'csstype';
import React from "react";
import ElevatorComponent from "./ElevatorComponent";

export interface Elevator {
    id: string,
    currentFloor: number,
    status: string,
    requests: ElevatorRequest[]
}


export interface Elevators {
    elevators: Elevator[]
}

export interface ElevatorRequest {
    id: string,
    requestStatus: string,
    floor: number,
}

const elevatorContainerStyle: CSS.Properties = {
    display: 'flex',
    flexDirection: 'row',
    flexWrap: 'wrap'
};

const ElevatorContainer = ({elevators}: Elevators) => {
    return (

        <div style={elevatorContainerStyle}>
            {elevators.map(elevator =>
                <ElevatorComponent key={elevator.id} id={elevator.id} currentFloor={elevator.currentFloor}
                                   status={elevator.status}  requests={elevator.requests} />
            )}
        </div>
    )
};

export default ElevatorContainer;
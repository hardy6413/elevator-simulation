import {Elevator} from "./Elevator";
import CSS from "csstype";
import {useMutation, useQueryClient} from "react-query";
import {createRequest} from "./ElevatorService";

const maxFloors = 16;

const ElevatorComponent = ({id, currentFloor, status, requests}: Elevator) => {
    useQueryClient();
    const addElevatorMutation = useMutation(createRequest)
    const currentFloorElementId = `${String(currentFloor)}${id}`
    const currentFloorElement = document.getElementById(currentFloorElementId)

    if (currentFloorElement) {
        if (status === 'OPENED') {
            currentFloorElement.style.borderColor = "green";
        } else if (status === "WAITING") {
            currentFloorElement.style.borderColor = "green";
        }
        setTimeout(() => {
            currentFloorElement.style.borderColor = "transparent";
        }, 100)
    }

    const chooseFloor = async (elevatorId: string, floor: number) => {
        const elementId = `${String(floor)}${elevatorId}`
        const element = document.getElementById(elementId)
        if (element) {
            element.style.borderColor = "red";
        }
        addElevatorMutation.mutate({elevatorId, floor})
    }


    return (
        <div style={elevatorStyle}>
            <div style={floorStyle}>{currentFloor}</div>
            <div style={numberContainerStyle}>
                {
                    Array.from({length: maxFloors}).map((_, index) => (
                        <p id={`${String(index)}${id}`} key={index} style={numberStyle}
                           onClick={() => chooseFloor(id, index)}>{index}</p>
                    ))
                }
            </div>
        </div>
    )
};

export default ElevatorComponent;

//css
const elevatorStyle: CSS.Properties = {
    backgroundColor: 'grey',
    margin: '1rem',
    width: '300px',
    height: '400px',
    display: 'flex',
    flexDirection: 'column',
};

const floorStyle: CSS.Properties = {
    backgroundColor: 'black',
    color: 'red',
    padding: '0.5rem',
    fontSize: '2rem',
    margin: '1rem',
    alignSelf: 'center',
};

const numberStyle: CSS.Properties = {
    backgroundColor: 'white',
    color: 'black',
    padding: '0.5rem',
    fontSize: '1rem',
    margin: '1rem',
    alignSelf: 'center',
    minWidth: '20px',
    minHeight: '20px',
    textAlign: 'center',
    borderRadius: '0.75rem',
    borderStyle: 'solid',
    borderColor: 'transparent',
    borderWidth: '0.2rem'
};

const numberContainerStyle: CSS.Properties = {
    display: 'flex',
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
    alignSelf: 'center',
};
import React from 'react';
import {useMutation, useQuery, useQueryClient} from "react-query";
import {createElevator, getElevators, run} from "./ElevatorService";
import ElevatorContainer, {Elevator} from "./Elevatos";

function App() {
    const queryClient = useQueryClient()

    const addElevatorMutation = useMutation(createElevator, {
        onSuccess: () => {
            queryClient.invalidateQueries('elevators')
        },
    })

    const addElevator = async () => {
        addElevatorMutation.mutate()
    }

    const updateElevatorsMutation = useMutation(run)

    const runElevators = (elevatorId: string) => {
        updateElevatorsMutation.mutate(elevatorId)
    }

    const simulate = async () => {
        elevators.forEach(
            (e) => runElevators(e.id)
        )
    }

    const result = useQuery('elevators', getElevators,
        {refetchInterval: 1000}
    )

    if (result.isLoading) {
        return <div>waiting for backend....</div>
    }
    console.log(result.data)
    const elevators: Elevator[] = result.data ? result.data : []

    return (
        <div>
            <button onClick={() => addElevator()}>add elevator</button>
            <button onClick={() => simulate()}>simulate</button>
            <ElevatorContainer elevators={elevators}/>
        </div>
    )

}

export default App;

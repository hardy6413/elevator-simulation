import {axiosInstance} from "./axiosUtils";

export interface SubmittedRequest {
    elevatorId: string,
    floor: number
}

const elevatorUrl = 'http://localhost:8080/elevator';
const requestUrl = 'http://localhost:8080/elevator/request';

export const createElevator = () => axiosInstance.post(elevatorUrl).then(res => res.data)

export const getElevators = () => axiosInstance.get(elevatorUrl).then(res => res.data)

export const createRequest = (submittedRequest: SubmittedRequest) => axiosInstance.post(requestUrl, submittedRequest).then(res => res.data)

export const runElevator = (elevatorId: string) =>
    axiosInstance.put(`${elevatorUrl}/${elevatorId}/run`).then(res => res.data)

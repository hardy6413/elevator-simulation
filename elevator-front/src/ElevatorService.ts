import {axiosInstance} from "./axiosUtils";
import {SubmitedRequest} from "./SubmitedRequest";

const elevatorUrl = 'http://localhost:8080/elevator';
const requestUrl = 'http://localhost:8080/elevator/request';

export const createElevator = () => axiosInstance.post(elevatorUrl).then(res => res.data)

export const getElevators = () => axiosInstance.get(elevatorUrl).then(res => res.data)

export const createRequest = (submittedRequest: SubmitedRequest) => axiosInstance.post(requestUrl, submittedRequest).then(res => res.data)

export const run = (elevatorId: string) =>
    axiosInstance.put(`${elevatorUrl}/${elevatorId}/run`).then(res => res.data)

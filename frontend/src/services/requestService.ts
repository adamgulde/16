import axios from 'axios';
import type { ConnectionRequest } from './types';

const API_URL = '/api/requests';

export const sendRequest = async (senderId: string, receiverId: string) => {
  await axios.post(API_URL, { senderId, receiverId });
};

export const fetchRequests = async (userId: string): Promise<ConnectionRequest[]> => {
  const response = await axios.get<ConnectionRequest[]>(`${API_URL}/${userId}`);
  return response.data;
};

export const acceptRequest = async (requestId: string) => {
  await axios.put(`${API_URL}/${requestId}/accept`);
};

export const findPath = async (senderId: string, receiverId: string): Promise<string[]> => {
  const response = await axios.get<string[]>(`/api/users/${senderId}/path/${receiverId}`);
  return response.data;
};

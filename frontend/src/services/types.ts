import type { User } from './userService';

export const RequestStatus = {
  PENDING: 'PENDING',
  ACCEPTED: 'ACCEPTED',
  REJECTED: 'REJECTED'
} as const;

export type RequestStatus = typeof RequestStatus[keyof typeof RequestStatus];

export interface ConnectionRequest {
  id: string;
  sender: User;
  receiver: User;
  status: RequestStatus;
}

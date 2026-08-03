import axios from 'axios';

export interface User {
  id: string;
  name: string;
  phoneNumber: number;
  relationships: string[];
}

export interface Node {
  id: string;
  name: string;
  phoneNumber: number;
  val: number;
}

export interface Link {
  source: string;
  target: string;
}

export interface GraphData {
  nodes: Node[];
  links: Link[];
}

const API_URL = '/api/users';

export const fetchUsers = async (): Promise<User[]> => {
  const response = await axios.get<User[]>(API_URL);
  return response.data;
};

export const transformToGraphData = (users: User[]): GraphData => {
  const nodes: Node[] = users.map((user) => ({
    id: user.id,
    name: user.name,
    phoneNumber: user.phoneNumber,
    val: 5,
  }));

  const linkSet = new Set<string>();
  const links: Link[] = [];

  users.forEach((user) => {
    user.relationships.forEach((targetId) => {
      // Create a unique key for A-B relationship (undirected)
      const key = [user.id, targetId].sort().join('-');
      
      // Only add if the link hasn't been added yet AND the target exists
      if (!linkSet.has(key) && users.some(u => u.id === targetId)) {
        linkSet.add(key);
        links.push({
          source: user.id,
          target: targetId,
        });
      }
    });
  });

  return { nodes, links };
};

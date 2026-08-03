import { useEffect, useState, useRef, useMemo } from 'react';
import ForceGraph2D from 'react-force-graph-2d';
import { fetchUsers, transformToGraphData } from '../services/userService';
import { sendRequest, fetchRequests, acceptRequest, findPath } from '../services/requestService';
import type { User, GraphData } from '../services/userService';
import type { ConnectionRequest } from '../services/types';
import { UserCard } from './UserCard';

interface GraphContainerProps {
  currentUser: User;
}

export const GraphContainer = ({ currentUser }: GraphContainerProps) => {
  const [graphData, setGraphData] = useState<GraphData>({ nodes: [], links: [] });
  const [users, setUsers] = useState<User[]>([]);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [requests, setRequests] = useState<ConnectionRequest[]>([]);
  const fgRef = useRef<any>(null);
  const [highlightNodes, setHighlightNodes] = useState<Set<string>>(new Set());
  const [highlightLinks, setHighlightLinks] = useState<Set<string>>(new Set());
  const [searchTerm, setSearchTerm] = useState('');

  const nodeConnectionCounts = useMemo(() => {
    const counts: Map<string, number> = new Map();
    users.forEach(user => {
      counts.set(user.id, user.relationships.length);
    });
    return counts;
  }, [users]);

  const filteredGraphData = useMemo(() => {
    if (!searchTerm) return graphData;
    const filteredNodes = graphData.nodes.filter(node => node.name.toLowerCase().includes(searchTerm.toLowerCase()));
    const filteredNodeIds = new Set(filteredNodes.map(n => n.id));
    const filteredLinks = graphData.links.filter(l => 
        filteredNodeIds.has(typeof l.source === 'string' ? l.source : (l.source as any).id) &&
        filteredNodeIds.has(typeof l.target === 'string' ? l.target : (l.target as any).id)
    );
    return { nodes: filteredNodes, links: filteredLinks };
  }, [graphData, searchTerm]);

  // Helper to interpolate color: 0 connections = Green (#008000), 10+ = Grey (#808080)
  const getColorForConnections = (count: number) => {
    const intensity = Math.min(count / 10, 1);
    const r = Math.round(0 + (128 - 0) * intensity);
    const g = Math.round(128 + (128 - 128) * intensity);
    const b = Math.round(0 + (128 - 0) * intensity);
    return `rgb(${r}, ${g}, ${b})`;
  };

  const loadData = async () => {
    const usersData = await fetchUsers();
    setUsers(usersData);
    setGraphData(transformToGraphData(usersData));
    const requestsData = await fetchRequests(currentUser.id);
    setRequests(requestsData);
  };

  useEffect(() => {
    loadData();
  }, [currentUser]);

  useEffect(() => {
    if (fgRef.current) {
      // @ts-ignore
      fgRef.current.d3Force('charge').strength(-50); // Even less spread out
      // @ts-ignore
      fgRef.current.d3Force('link').distance(20); // Closer links
    }
  }, [graphData]);

  const myRequests = useMemo(() => {
    return requests.filter(req => req.sender.id === currentUser.id || req.receiver.id === currentUser.id);
  }, [requests, currentUser.id]);

  const getRelationshipNames = (relationshipIds: string[]) => {
    return relationshipIds
      .map((id) => users.find((u) => u.id === id)?.name)
      .filter(Boolean) as string[];
  };

  const updateHighlight = (node: any) => {
    if (node) {
      const neighbors = new Set<string>();
      const links = new Set<string>();

      neighbors.add(node.id);
      graphData.links.forEach((link) => {
        const sourceId = typeof link.source === 'string' ? link.source : (link.source as any).id;
        const targetId = typeof link.target === 'string' ? link.target : (link.target as any).id;
        
        if (sourceId === node.id || targetId === node.id) {
          links.add(`${sourceId}-${targetId}`);
          neighbors.add(sourceId);
          neighbors.add(targetId);
        }
      });
      setHighlightNodes(neighbors);
      setHighlightLinks(links);
    } else {
      setHighlightNodes(new Set());
      setHighlightLinks(new Set());
    }
  };

  const handleFindPath = async (targetId: string) => {
    const path = await findPath(currentUser.id, targetId);
    const pathNodes = new Set(path);
    const pathLinks = new Set<string>();
    for (let i = 0; i < path.length - 1; i++) {
        pathLinks.add(`${path[i]}-${path[i+1]}`);
        pathLinks.add(`${path[i+1]}-${path[i]}`);
    }
    setHighlightNodes(pathNodes);
    setHighlightLinks(pathLinks);
  };

  const handleSendRequest = async (receiverId: string) => {
    await sendRequest(currentUser.id, receiverId);
    loadData(); // Refresh requests
  };

  const handleAcceptRequest = async (requestId: string) => {
    await acceptRequest(requestId);
    loadData(); // Refresh requests and user data
  };

  return (
    <div style={{ width: '100%', height: '100vh', display: 'flex', flexDirection: 'column' }}>
      <div style={{ padding: '10px', background: '#f0f0f0', display: 'flex', gap: '10px', alignItems: 'center' }}>
        <input 
            type="text" 
            placeholder="Search users..." 
            value={searchTerm} 
            onChange={(e) => setSearchTerm(e.target.value)} 
            style={{ padding: '8px', flex: 1 }}
        />
      </div>
      <div style={{ flex: 1, display: 'flex', position: 'relative' }}>
        {/* Docked Notification Panel */}
        <div style={{ 
            width: '250px', 
            background: 'white', 
            borderRight: '1px solid #ccc', 
            padding: '10px',
            overflowY: 'auto'
        }}>
            <h3>Notifications ({myRequests.length})</h3>
            {myRequests.map(req => (
              <div key={req.id} style={{ border: '1px solid #ddd', margin: '5px 0', padding: '10px', borderRadius: '4px' }}>
                <p><strong>{req.sender.name}</strong> wants to connect</p>
                <div style={{ display: 'flex', gap: '5px', alignItems: 'center' }}>
                  <p>Status: {req.status}</p>
                  {req.status === 'PENDING' && req.receiver.id === currentUser.id && (
                    <button onClick={() => handleAcceptRequest(req.id)}>Accept</button>
                  )}
                </div>
              </div>
            ))}
        </div>

        <div style={{ flex: 1, position: 'relative' }}>
        <ForceGraph2D
          ref={fgRef}
          graphData={filteredGraphData}
          nodeCanvasObject={(node, ctx, globalScale) => {
            const isHighlighted = highlightNodes.has(node.id as string);
            
            const connectionCount = nodeConnectionCounts.get(node.id as string) ?? 0;
            const nodeColor = node.id === currentUser.id ? 'blue' : getColorForConnections(connectionCount);

            ctx.beginPath();
            ctx.arc(node.x!, node.y!, isHighlighted ? 12 : 8, 0, 2 * Math.PI, false);
            ctx.fillStyle = isHighlighted ? 'red' : nodeColor;
            ctx.fill();

            const label = (node as any).name;
            const fontSize = 16 / globalScale;
            ctx.font = `${fontSize}px Sans-Serif`;
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillStyle = isHighlighted ? 'red' : 'black';
            ctx.fillText(label, node.x!, node.y! + 15);
          }}
          linkColor={(link: any) => {
            const sourceId = typeof link.source === 'string' ? link.source : (link.source as any).id;
            const targetId = typeof link.target === 'string' ? link.target : (link.target as any).id;
            return highlightLinks.has(`${sourceId}-${targetId}`) || highlightLinks.has(`${targetId}-${sourceId}`)
              ? 'red'
              : '#999';
          }}
          onNodeClick={(node: any) => {
            updateHighlight(node);
            const user = users.find((u) => u.id === node.id);
            if (user) setSelectedUser(user);
          }}
          onBackgroundClick={() => updateHighlight(null)}
        />
        
        {selectedUser && (
          <UserCard
            user={selectedUser}
            relationshipNames={getRelationshipNames(selectedUser.relationships)}
            onClose={() => {
              setSelectedUser(null);
              updateHighlight(null);
            }}
            onSendRequest={() => handleSendRequest(selectedUser.id)}
            showSendRequest={selectedUser.id !== currentUser.id && !currentUser.relationships.includes(selectedUser.id)}
            onFindPath={() => handleFindPath(selectedUser.id)}
            showFindPath={selectedUser.id !== currentUser.id && !currentUser.relationships.includes(selectedUser.id)}
            onSelectNode={(nodeId: string) => {
                const node = filteredGraphData.nodes.find(n => n.id === nodeId);
                if (node) {
                    updateHighlight(node);
                    const user = users.find((u) => u.id === node.id);
                    if (user) setSelectedUser(user);
                }
            }}
            isCurrentUser={selectedUser.id === currentUser.id}
            onEdit={() => alert('Edit profile functionality to be implemented')}
          />
        )}
        </div>
      </div>
    </div>
  );
};

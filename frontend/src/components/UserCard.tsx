import type { User } from '../services/userService';

interface UserCardProps {
  user: User;
  relationshipNames: string[];
  onClose: () => void;
  onSendRequest?: () => void;
  showSendRequest?: boolean;
  onFindPath?: () => void;
  showFindPath?: boolean;
  onSelectNode: (nodeId: string) => void;
  isCurrentUser: boolean;
  onEdit?: () => void;
}

export const UserCard = ({ user, relationshipNames, onClose, onSendRequest, showSendRequest, onFindPath, showFindPath, onSelectNode, isCurrentUser, onEdit }: UserCardProps) => {
  return (
    <div className="user-card" style={{ position: 'absolute', top: 10, right: 10, padding: '1rem', background: 'white', border: '1px solid #ccc', borderRadius: '8px', zIndex: 10 }}>
      <h3>{user.name}</h3>
      <p>Phone: {user.phoneNumber}</p>
      <p>Connections:</p>
      <ul>
        {user.relationships.map((relId, index) => (
            <li key={relId}>
                <button onClick={() => onSelectNode(relId)} style={{ background: 'none', border: 'none', color: 'blue', cursor: 'pointer', textDecoration: 'underline' }}>
                    {relationshipNames[index]}
                </button>
            </li>
        ))}
      </ul>
      {showSendRequest && <button onClick={onSendRequest}>Send Connection Request</button>}
      {showFindPath && <button onClick={onFindPath}>Find Path</button>}
      {isCurrentUser && <button onClick={onEdit}>Edit Profile</button>}
      <button onClick={onClose}>Close</button>
    </div>
  );
};

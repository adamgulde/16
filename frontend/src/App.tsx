import { useState } from 'react';
import { GraphContainer } from './components/GraphContainer';
import { Signin } from './components/Signin';
import type { User } from './services/userService';

function App() {
  const [currentUser, setCurrentUser] = useState<User | null>(null);

  return (
    <div className="app-container">
      {currentUser ? (
        <GraphContainer currentUser={currentUser} />
      ) : (
        <Signin onLogin={setCurrentUser} />
      )}
    </div>
  );
}

export default App

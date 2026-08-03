import { useState } from 'react';
import axios from 'axios';
import type { User } from '../services/userService';

interface SigninProps {
  onLogin: (user: User) => void;
}

export const Signin = ({ onLogin }: SigninProps) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [isRegistering, setIsRegistering] = useState(false);
  const [error, setError] = useState('');

  const handleAuth = async (isRegister: boolean) => {
    try {
      const endpoint = isRegister ? '/api/auth/register' : '/api/auth/login';
      const payload = isRegister ? { username, password, firstName, lastName, phoneNumber } : { username };
      const response = await axios.post<User>(endpoint, payload);
      onLogin(response.data);
    } catch (err: any) {
      setError(err.response?.data || 'An error occurred');
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100vh', gap: '10px' }}>
      <h2>Sign In / Register</h2>
      <input type="text" placeholder="Username" value={username} onChange={(e) => setUsername(e.target.value)} />
      <input type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} />
      
      {isRegistering && (
          <>
            <input type="text" placeholder="First Name" value={firstName} onChange={(e) => setFirstName(e.target.value)} />
            <input type="text" placeholder="Last Name" value={lastName} onChange={(e) => setLastName(e.target.value)} />
            <input type="text" placeholder="Phone Number" value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} />
          </>
      )}

      <div style={{ display: 'flex', gap: '10px' }}>
        <button onClick={() => isRegistering ? handleAuth(true) : setIsRegistering(true)}>
            {isRegistering ? 'Register' : 'Go to Register'}
        </button>
        {!isRegistering && <button onClick={() => handleAuth(false)}>Login</button>}
      </div>
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </div>
  );
};

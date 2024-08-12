import { useState, useEffect } from 'react';
import axios from 'axios';
import { useRouter } from 'next/router';
import Link from 'next/link';

export default function Login() {
  const [userid, setUserid] = useState('');
  const [password, setPassword] = useState('');
  const router = useRouter();
  const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL;

  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        const response = await axios.get(`${backendUrl}/api/users/check-auth`, { withCredentials: true });
        if (response.data.username) { 
          router.push('/home');
        }
      } catch (error) {
        console.error('Error checking login status', error);
      }
    };

    checkLoginStatus();
  }, [backendUrl, router]); 

  const handleLogin = async () => {
    try {
      const response = await axios.post(`${backendUrl}/api/users/login`, { userid, password }, { withCredentials: true });
      alert('Login successful');
      router.push('/home');
    } catch (error) {
      alert('Login failed');
    }
  };

  return (
    <div>
      <h2>Login</h2>
      <input
        type="text"
        placeholder="Userid"
        value={userid}
        onChange={(e) => setUserid(e.target.value)}
      />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <button onClick={handleLogin}>Login</button>
      <div>
        <Link href="/register">
          <a>Sign Up</a>
        </Link>
      </div>
    </div>
  );
}

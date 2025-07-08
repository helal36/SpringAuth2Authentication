import React, { useState, useEffect } from 'react';
import { createRoot } from 'react-dom/client'; // Import createRoot from react-dom
import axios from 'axios';

function App() {
  const [token, setToken] = useState(null);
  const [balance, setBalance] = useState('');

  // Login function redirects to the Authorization Server's URL
  const login = () => {
    alert("You will be redirected to MyBank's login page. Please log in to continue.");
    window.location = `http://localhost:9000/oauth2/authorize?response_type=code&` +
      `client_id=fundtracker-client&` +
      `redirect_uri=${encodeURIComponent('http://localhost:3000/callback')}&` +
      `scope=read:balance`;
  };

  // Logout function clears local state and redirects to auth server logout
  const logout = () => {
    // Clear the token and balance in the client app
    setToken(null);
    setBalance('');

    // Redirect to the auth server's /logout endpoint with post_logout_redirect_uri
    window.location = `http://localhost:9000/logout?post_logout_redirect_uri=${encodeURIComponent('http://localhost:3000')}`;
  };

  // Handle access token retrieval after redirect back to the app
  useEffect(() => {
    const code = new URLSearchParams(window.location.search).get('code');
    if (code && !token) {
      axios.post('http://localhost:8083/token', { code })
        .then(res => setToken(res.data.access_token))
        .catch(console.error);
    }
  }, [token]);

  // Fetch bank balance when token is available
  useEffect(() => {
    if (token) {
      axios.get('http://localhost:9001/balance', {
        headers: { Authorization: `Bearer ${token}` }
      })
        .then(res => setBalance(res.data))
        .catch(console.error);
    }
  }, [token]);

  return (
    <div style={{ padding: 20 }}>
      <h1>FundTracker</h1>
      {/* Show login button before authentication */}
      {!token && <button onClick={login}>Connect to MyBank</button>}
      {/* Display the token and balance after successful authentication */}
      {token && (
        <>
          <p><strong>Access Token:</strong> {token}</p>
          <h2>Your Bank Balance:</h2>
          <pre>{balance}</pre>
          <button onClick={logout}>Logout</button> {/* Logout button */}
        </>
      )}
    </div>
  );
}

// Use createRoot instead of ReactDOM.render
const rootElement = document.getElementById('root');
const root = createRoot(rootElement);
root.render(<App />);
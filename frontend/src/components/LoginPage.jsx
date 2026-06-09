import React, { useState } from 'react';
import { loginUser, registerUser } from '../api/AuthPageApi';

function AuthPage({ onLogin = () => {} }) {
  const [activeTab, setActiveTab] = useState('login');

  const [loginData, setLoginData] = useState({
    username: '',
    password: '',
  });

  const [signupData, setSignupData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    location: '',
    phoneNumber: '',
  });

  const [message, setMessage] = useState('');

  const isSuccessMessage =
    message.toLowerCase().includes('successful') ||
    message.toLowerCase().includes('success');

  const handleLogin = async (e) => {
    e.preventDefault();
    setMessage('');

    if (!loginData.username || !loginData.password) {
      setMessage('Please fill in all fields');
      return;
    }

    try {
      const result = await loginUser({
        username: loginData.username,
        rawPassword: loginData.password,
      });

      if (result?.token) {
        localStorage.setItem('token', result.token);
      }

      setMessage('Login successful');
      onLogin(result?.username || loginData.username);
    } catch (error) {
      setMessage(error.message || 'Invalid username or password');
    }
  };

  const handleSignup = async (e) => {
    e.preventDefault();
    setMessage('');

    if (
      !signupData.username ||
      !signupData.email ||
      !signupData.password ||
      !signupData.confirmPassword ||
      !signupData.location
    ) {
      setMessage('Please fill in all required fields');
      return;
    }

    if (signupData.password !== signupData.confirmPassword) {
      setMessage('Passwords do not match');
      return;
    }

    try {
      await registerUser({
        username: signupData.username,
        email: signupData.email,
        rawPassword: signupData.password,
        location: signupData.location,
        phoneNumber: signupData.phoneNumber,
      });

      setMessage('Account created successfully! You can now log in.');
      setLoginData({
        username: signupData.username,
        password: '',
      });
      setSignupData({
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        location: '',
        phoneNumber: '',
      });
      setActiveTab('login');
    } catch (error) {
      setMessage(error.message || 'Failed to create account');
    }
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        backgroundColor: '#fff',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '1rem',
      }}
    >
      <div style={{ width: '100%', maxWidth: '400px' }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <div
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '0.75rem',
              marginBottom: '1rem',
            }}
          >
            <div
              style={{
                backgroundColor: '#ffa500',
                color: '#000',
                width: '64px',
                height: '64px',
                border: '4px solid #000',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontWeight: 'bold',
                fontSize: '1.875rem',
                boxShadow: '6px 6px 0px rgba(45,27,0,1)',
                transform: 'rotate(-3deg)',
              }}
            >
              OP
            </div>
          </div>

          <h1
            style={{
              fontWeight: 'bold',
              fontSize: '2.25rem',
              marginBottom: '0.5rem',
            }}
          >
            OpenPantry
          </h1>

          <p style={{ fontSize: '1rem', color: '#666' }}>
            Sign in or create an account to continue
          </p>
        </div>

        <div style={{ marginBottom: '2rem' }}>
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: '1fr 1fr',
              border: '4px solid #000',
              boxShadow: '4px 4px 0px rgba(45,27,0,1)',
              backgroundColor: '#fff',
            }}
          >
            <button
              type="button"
              onClick={() => {
                setActiveTab('login');
                setMessage('');
              }}
              style={{
                padding: '1rem',
                backgroundColor: activeTab === 'login' ? '#000' : '#fff',
                color: activeTab === 'login' ? '#fff' : '#000',
                fontWeight: 'bold',
                fontSize: '1.125rem',
                border: 'none',
                cursor: 'pointer',
                borderRight: activeTab === 'login' ? 'none' : '2px solid #000',
              }}
            >
              LOGIN
            </button>

            <button
              type="button"
              onClick={() => {
                setActiveTab('signup');
                setMessage('');
              }}
              style={{
                padding: '1rem',
                backgroundColor: activeTab === 'signup' ? '#000' : '#fff',
                color: activeTab === 'signup' ? '#fff' : '#000',
                fontWeight: 'bold',
                fontSize: '1.125rem',
                border: 'none',
                cursor: 'pointer',
                borderLeft: activeTab === 'signup' ? 'none' : '2px solid #000',
              }}
            >
              SIGN UP
            </button>
          </div>
        </div>

        {message && (
          <div
            style={{
              padding: '1rem',
              marginBottom: '1rem',
              backgroundColor: isSuccessMessage ? '#e8f5e9' : '#ffebee',
              border: `2px solid ${isSuccessMessage ? '#4caf50' : '#d32f2f'}`,
              borderRadius: '4px',
              color: isSuccessMessage ? '#2e7d32' : '#c62828',
              fontWeight: 'bold',
            }}
          >
            {message}
          </div>
        )}

        {activeTab === 'login' && (
          <div
            style={{
              backgroundColor: '#fff',
              border: '4px solid #000',
              boxShadow: '8px 8px 0px rgba(45,27,0,1)',
              padding: '1.5rem',
              marginBottom: '2rem',
            }}
          >
            <form
              onSubmit={handleLogin}
              style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}
            >
              <div>
                <label
                  style={{
                    display: 'block',
                    fontWeight: 'bold',
                    marginBottom: '0.5rem',
                  }}
                >
                  USERNAME
                </label>

                <input
                  type="text"
                  placeholder="username"
                  value={loginData.username}
                  onChange={(e) =>
                    setLoginData({ ...loginData, username: e.target.value })
                  }
                  style={{
                    width: '90%',
                    padding: '0.75rem',
                    border: '4px solid #000',
                    boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                    fontFamily: 'inherit',
                  }}
                />
              </div>

              <div>
                <label
                  style={{
                    display: 'block',
                    fontWeight: 'bold',
                    marginBottom: '0.5rem',
                  }}
                >
                  PASSWORD
                </label>

                <input
                  type="password"
                  placeholder="••••••••"
                  value={loginData.password}
                  onChange={(e) =>
                    setLoginData({ ...loginData, password: e.target.value })
                  }
                  style={{
                    width: '90%',
                    padding: '0.75rem',
                    border: '4px solid #000',
                    boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                    fontFamily: 'inherit',
                  }}
                />
              </div>

              <button
                type="submit"
                style={{
                  width: '100%',
                  padding: '0.75rem 1.5rem',
                  backgroundColor: '#000',
                  color: '#fff',
                  fontWeight: 'bold',
                  border: '4px solid #000',
                  borderRadius: '4px',
                  boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                  cursor: 'pointer',
                  fontSize: '1.125rem',
                  marginTop: '1.5rem',
                }}
              >
                LOG IN
              </button>
            </form>
          </div>
        )}

        {activeTab === 'signup' && (
          <div
            style={{
              backgroundColor: '#fff',
              border: '4px solid #000',
              boxShadow: '8px 8px 0px rgba(45,27,0,1)',
              padding: '1.5rem',
              marginBottom: '2rem',
            }}
          >
            <form
              onSubmit={handleSignup}
              style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}
            >
              <div>
                <label
                  style={{
                    display: 'block',
                    fontWeight: 'bold',
                    marginBottom: '0.5rem',
                  }}
                >
                  USERNAME
                </label>

                <input
                  type="text"
                  placeholder="username"
                  value={signupData.username}
                  onChange={(e) =>
                    setSignupData({ ...signupData, username: e.target.value })
                  }
                  style={{
                    width: '90%',
                    padding: '0.75rem',
                    border: '4px solid #000',
                    boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                    fontFamily: 'inherit',
                  }}
                />
              </div>

              <div>
                <label
                  style={{
                    display: 'block',
                    fontWeight: 'bold',
                    marginBottom: '0.5rem',
                  }}
                >
                  EMAIL
                </label>

                <input
                  type="email"
                  placeholder="name@email.com"
                  value={signupData.email}
                  onChange={(e) =>
                    setSignupData({ ...signupData, email: e.target.value })
                  }
                  style={{
                    width: '90%',
                    padding: '0.75rem',
                    border: '4px solid #000',
                    boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                    fontFamily: 'inherit',
                  }}
                />
              </div>

              <div>
                <label
                  style={{
                    display: 'block',
                    fontWeight: 'bold',
                    marginBottom: '0.5rem',
                  }}
                >
                  PASSWORD
                </label>

                <input
                  type="password"
                  placeholder="••••••••"
                  value={signupData.password}
                  onChange={(e) =>
                    setSignupData({ ...signupData, password: e.target.value })
                  }
                  style={{
                    width: '90%',
                    padding: '0.75rem',
                    border: '4px solid #000',
                    boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                    fontFamily: 'inherit',
                  }}
                />
              </div>

              <div>
                <label
                  style={{
                    display: 'block',
                    fontWeight: 'bold',
                    marginBottom: '0.5rem',
                  }}
                >
                  CONFIRM PASSWORD
                </label>

                <input
                  type="password"
                  placeholder="••••••••"
                  value={signupData.confirmPassword}
                  onChange={(e) =>
                    setSignupData({
                      ...signupData,
                      confirmPassword: e.target.value,
                    })
                  }
                  style={{
                    width: '90%',
                    padding: '0.75rem',
                    border: '4px solid #000',
                    boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                    fontFamily: 'inherit',
                  }}
                />
              </div>

              <div>
                <label
                  style={{
                    display: 'block',
                    fontWeight: 'bold',
                    marginBottom: '0.5rem',
                  }}
                >
                  LOCATION
                </label>

                <input
                  type="text"
                  placeholder="Birmingham"
                  value={signupData.location}
                  onChange={(e) =>
                    setSignupData({ ...signupData, location: e.target.value })
                  }
                  style={{
                    width: '90%',
                    padding: '0.75rem',
                    border: '4px solid #000',
                    boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                    fontFamily: 'inherit',
                  }}
                />
              </div>

              <div>
                <label
                  style={{
                    display: 'block',
                    fontWeight: 'bold',
                    marginBottom: '0.5rem',
                  }}
                >
                  PHONE NUMBER
                </label>

                <input
                  type="tel"
                  placeholder="07123456789"
                  value={signupData.phoneNumber}
                  onChange={(e) =>
                    setSignupData({
                      ...signupData,
                      phoneNumber: e.target.value,
                    })
                  }
                  style={{
                    width: '90%',
                    padding: '0.75rem',
                    border: '4px solid #000',
                    boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                    fontFamily: 'inherit',
                  }}
                />
              </div>

              <button
                type="submit"
                style={{
                  width: '100%',
                  padding: '0.75rem 1.5rem',
                  backgroundColor: '#000',
                  color: '#fff',
                  fontWeight: 'bold',
                  border: '4px solid #000',
                  borderRadius: '4px',
                  boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                  cursor: 'pointer',
                  fontSize: '1.125rem',
                  marginTop: '1.5rem',
                }}
              >
                CREATE ACCOUNT
              </button>
            </form>
          </div>
        )}

        <div style={{ textAlign: 'center' }}>
          <p style={{ fontSize: '1.125rem', color: '#666' }}>
            Join our community and help reduce food waste!
          </p>
        </div>
      </div>
    </div>
  );
}

export default AuthPage;
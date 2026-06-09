import React, { useState } from 'react';
import { ShoppingBag, Plus, Bookmark, Settings, Users, Home } from 'lucide-react';

export function Header({ currentView = 'browse', onViewChange = () => {}, onLogout = () => {}, currentUser = '' }) {
  const [darkMode, setDarkMode] = useState(false);

  const headerBg = darkMode ? '#1a1a1a' : '#fff';
  const headerText = darkMode ? '#fff' : '#000';
  const borderColor = '#000';

  const buttonStyle = (isActive) => ({
    padding: '8px 16px',
    fontWeight: '500',
    borderRadius: '4px',
    border: `2px solid ${borderColor}`,
    boxShadow: '3px 3px 0px rgba(0,0,0,0.2)',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    backgroundColor: isActive ? '#000' : 'transparent',
    color: isActive ? '#fff' : headerText,
    transition: 'all 0.2s',
  });

  return (
    <header
      style={{
        backgroundColor: headerBg,
        borderBottom: `4px solid ${borderColor}`,
        position: 'sticky',
        top: 0,
        zIndex: 20,
        boxShadow: '0px 4px 0px rgba(0,0,0,0.2)',
      }}
    >
      <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '1rem' }}>
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
          }}
        >
          {/* Logo */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
            <div
              style={{
                backgroundColor: '#000',
                color: '#fff',
                width: '50px',
                height: '50px',
                border: '3px solid #000',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontWeight: 'bold',
                fontSize: '1.2rem',
                borderRadius: '8px',
              }}
            >
              OP
            </div>
            <div>
              <h1
                style={{
                  fontWeight: 'bold',
                  fontSize: '1.5rem',
                  margin: 0,
                  color: headerText,
                }}
              >
                OpenPantry
              </h1>
              <p
                style={{
                  fontSize: '0.75rem',
                  margin: 0,
                  opacity: 0.7,
                  color: headerText,
                }}
              >
              </p>
            </div>
          </div>

          {/* Tabs */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <button
              onClick={() => onViewChange('home')}
              style={buttonStyle(currentView === 'home')}
            >
              <Home size={18} />
              Home Page
            </button>

            <button
              onClick={() => onViewChange('browse')}
              style={buttonStyle(currentView === 'browse')}
            >
              <ShoppingBag size={18} />
              Browse Food
            </button>

            <button
              onClick={() => onViewChange('post')}
              style={buttonStyle(currentView === 'post')}
            >
              <Plus size={18} />
              Post Food
            </button>

            <button
              onClick={() => onViewChange('reservations')}
              style={buttonStyle(currentView === 'reservations')}
            >
              <Bookmark size={18} />

              My Reservations
            </button>

            <button
              onClick={() => onViewChange('communities')}
              style={buttonStyle(currentView === 'communities')}
            >
              <Users size={18} />
              Communities
            </button>

            <button
              onClick={() => onViewChange('settings')}
              style={buttonStyle(currentView === 'settings')}
              title="Settings"
            >
              <Settings size={18} />
              Settings
            </button>

            <button
              onClick={onLogout}
              style={{
                padding: '8px 16px',
                fontWeight: '500',
                borderRadius: '4px',
                border: '2px solid #000',
                boxShadow: '3px 3px 0px rgba(0,0,0,0.2)',
                cursor: 'pointer',
                backgroundColor: '#fff',
                color: '#d32f2f',
              }}
            >
              Logout
            </button>
          </div>
        </div>
      </div>
    </header>
  );
}

export default Header;
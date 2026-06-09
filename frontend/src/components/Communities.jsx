import React, { useEffect, useState } from 'react';
import { ArrowLeft, Plus, MapPin, X } from 'lucide-react';
import FoodCard from './FoodCard';
import {
  getCommunities,
  getCommunityById,
  createCommunity,
} from '../api/CommunityApi';

function Communities({
  foodItems = [],
  onReserve = () => {},
  onPostFood = () => {},
  a11y = {},
  joinedCommunities = [],
  onJoinedCommunitiesChange = () => {},
}) {
  const isDark = a11y.darkMode;
  const emptyBg = isDark ? '#1e1e1e' : '#f5f5f5';
  const emptyBorder = isDark ? '#888' : '#000';
  const emptyText = isDark ? '#aaa' : '#666';

  const [communities, setCommunities] = useState([]);
  const [selectedCommunity, setSelectedCommunity] = useState(null);
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true);

  const [showCreateForm, setShowCreateForm] = useState(false);
  const [creating, setCreating] = useState(false);
  const [newCommunity, setNewCommunity] = useState({
    name: '',
    location: '',
    description: '',
  });

  useEffect(() => {
    loadCommunities();
  }, []);

  const loadCommunities = async () => {
    try {
      setLoading(true);
      setMessage('');

      const data = await getCommunities();

      console.log('Communities response:', data);

      if (Array.isArray(data)) {
        setCommunities(data);
      } else if (Array.isArray(data?.content)) {
        setCommunities(data.content);
      } else {
        setCommunities([]);
      }
    } catch (error) {
      console.error('Failed to load communities:', error);
      setMessage(error.message || 'Failed to load communities');
      setCommunities([]);
    } finally {
      setLoading(false);
    }
  };

  const openCommunity = async (community) => {
    try {
      setMessage('');
      const fullCommunity = await getCommunityById(community.id);
      console.log('Single community response:', fullCommunity);
      setSelectedCommunity(fullCommunity);
    } catch (error) {
      console.error('Failed to load community details:', error);
      setMessage(error.message || 'Failed to load community details');
    }
  };

  const handleJoinLeave = (communityId) => {
    const updatedJoinedCommunities = joinedCommunities.includes(communityId)
      ? joinedCommunities.filter((id) => id !== communityId)
      : [...joinedCommunities, communityId];

    onJoinedCommunitiesChange(updatedJoinedCommunities);
  };

  const handleCreateCommunity = async (e) => {
    e.preventDefault();

    if (!newCommunity.name.trim() || !newCommunity.location.trim()) {
      setMessage('Community name and location are required.');
      return;
    }

    try {
      setCreating(true);
      setMessage('');

      const payload = {
        name: newCommunity.name.trim(),
        location: newCommunity.location.trim(),
        description: newCommunity.description.trim(),
      };

      const created = await createCommunity(payload);
      console.log('Created community:', created);

      setNewCommunity({
        name: '',
        location: '',
        description: '',
      });

      setShowCreateForm(false);
      setMessage('Community created successfully.');

      await loadCommunities();

      if (created?.id) {
        setSelectedCommunity(created);
      }
    } catch (error) {
      console.error('Failed to create community:', error);
      setMessage(error.message || 'Failed to create community');
    } finally {
      setCreating(false);
    }
  };

  const getCommunityFoodCount = (communityName) =>
    foodItems.filter((item) => item.community === communityName).length;

  const buttonStyle = {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    padding: '0.6rem 1rem',
    backgroundColor: '#fff',
    color: '#000',
    fontWeight: 'bold',
    border: '2px solid #000',
    borderRadius: '4px',
    boxShadow: '3px 3px 0px rgba(45,27,0,1)',
    cursor: 'pointer',
  };

  const blackButtonStyle = {
    padding: '0.6rem 1rem',
    backgroundColor: '#000',
    color: '#fff',
    fontWeight: 'bold',
    border: '2px solid #000',
    borderRadius: '4px',
    boxShadow: '3px 3px 0px rgba(45,27,0,1)',
    cursor: 'pointer',
  };

  if (loading) {
    return (
      <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '2rem 1rem' }}>
        <p>Loading communities...</p>
      </div>
    );
  }

  if (selectedCommunity) {
    const communityFoodItems = foodItems.filter(
      (item) => item.community === selectedCommunity.name
    );

    const isJoined = joinedCommunities.includes(selectedCommunity.id);

    return (
      <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '2rem 1rem' }}>
        {message && (
          <div style={{ marginBottom: '1rem', color: 'red', fontWeight: 'bold' }}>
            {message}
          </div>
        )}

        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            marginBottom: '2rem',
            flexWrap: 'wrap',
            gap: '1rem',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <button
              onClick={() => setSelectedCommunity(null)}
              style={buttonStyle}
            >
              <ArrowLeft size={16} />
              Back
            </button>

            <div>
              <h2 style={{ fontSize: '1.75rem', fontWeight: 'bold', margin: 0 }}>
                {selectedCommunity.name}
              </h2>

              <p style={{ color: '#666', margin: '0.25rem 0 0 0', fontSize: '0.9rem' }}>
                {selectedCommunity.description || 'No description available.'}
              </p>

              <p
                style={{
                  color: '#666',
                  margin: '0.4rem 0 0 0',
                  fontSize: '0.85rem',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.35rem',
                }}
              >
                <MapPin size={14} />
                {selectedCommunity.location || 'No location provided'}
              </p>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
            {isJoined && (
              <button
                onClick={() => onPostFood(selectedCommunity.name)}
                style={buttonStyle}
              >
                <Plus size={16} />
                Post Food Here
              </button>
            )}

            <button
              onClick={() => handleJoinLeave(selectedCommunity.id)}
              style={{
                ...blackButtonStyle,
                backgroundColor: isJoined ? '#fff' : '#000',
                color: isJoined ? '#000' : '#fff',
              }}
            >
              {isJoined ? 'Leave Community' : 'Join Community'}
            </button>
          </div>
        </div>

        {!isJoined ? (
          <div
            style={{
              textAlign: 'center',
              padding: '3rem',
              backgroundColor: emptyBg,
              border: `2px solid ${emptyBorder}`,
              borderRadius: '4px',
            }}
          >
            <p style={{ fontSize: '1.1rem', color: emptyText, marginBottom: '1rem' }}>
              Join this community to see and reserve food posts.
            </p>

            <button
              onClick={() => handleJoinLeave(selectedCommunity.id)}
              style={blackButtonStyle}
            >
              Join {selectedCommunity.name}
            </button>
          </div>
        ) : communityFoodItems.length === 0 ? (
          <div
            style={{
              textAlign: 'center',
              padding: '3rem',
              backgroundColor: emptyBg,
              border: `2px solid ${emptyBorder}`,
              borderRadius: '4px',
            }}
          >
            <p style={{ fontSize: '1.1rem', color: emptyText, marginBottom: '1rem' }}>
              No food posts in this community yet.
            </p>

            <button
              onClick={() => onPostFood(selectedCommunity.name)}
              style={blackButtonStyle}
            >
              Be the first to post food here
            </button>
          </div>
        ) : (
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
              gap: '20px',
            }}
          >
            {communityFoodItems.map((item) => (
              <FoodCard
                key={item.id}
                item={item}
                onReserve={onReserve}
              />
            ))}
          </div>
        )}
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '2rem 1rem' }}>
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginBottom: '2rem',
          gap: '1rem',
          flexWrap: 'wrap',
        }}
      >
        <h2 style={{ fontSize: '1.75rem', fontWeight: 'bold', margin: 0 }}>
          Communities
        </h2>

        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', flexWrap: 'wrap' }}>
          <p style={{ color: '#666', margin: 0 }}>
            {joinedCommunities.length} joined
          </p>

          <button
            onClick={() => {
              setMessage('');
              setShowCreateForm(true);
            }}
            style={buttonStyle}
          >
            <Plus size={16} />
            Create Community
          </button>
        </div>
      </div>

      {message && (
        <div style={{ marginBottom: '1rem', color: 'red', fontWeight: 'bold' }}>
          {message}
        </div>
      )}

      {showCreateForm && (
        <div
          style={{
            marginBottom: '2rem',
            border: '3px solid #000',
            borderRadius: '6px',
            padding: '1.5rem',
            backgroundColor: '#fff',
            boxShadow: '6px 6px 0px rgba(45,27,0,1)',
          }}
        >
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              marginBottom: '1rem',
            }}
          >
            <h3 style={{ margin: 0, fontSize: '1.25rem' }}>Create Community</h3>

            <button
              onClick={() => setShowCreateForm(false)}
              style={{
                background: 'transparent',
                border: 'none',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
              }}
            >
              <X size={20} />
            </button>
          </div>

          <form onSubmit={handleCreateCommunity}>
            <div style={{ display: 'grid', gap: '1rem' }}>
              <div>
                <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.4rem' }}>
                  Community Name
                </label>
                <input
                  type="text"
                  value={newCommunity.name}
                  onChange={(e) =>
                    setNewCommunity((prev) => ({ ...prev, name: e.target.value }))
                  }
                  placeholder="Enter community name"
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '2px solid #000',
                    borderRadius: '4px',
                    fontSize: '1rem',
                    boxSizing: 'border-box',
                  }}
                />
              </div>

              <div>
                <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.4rem' }}>
                  Location
                </label>
                <input
                  type="text"
                  value={newCommunity.location}
                  onChange={(e) =>
                    setNewCommunity((prev) => ({ ...prev, location: e.target.value }))
                  }
                  placeholder="Enter location"
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '2px solid #000',
                    borderRadius: '4px',
                    fontSize: '1rem',
                    boxSizing: 'border-box',
                  }}
                />
              </div>

              <div>
                <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.4rem' }}>
                  Description
                </label>
                <textarea
                  value={newCommunity.description}
                  onChange={(e) =>
                    setNewCommunity((prev) => ({ ...prev, description: e.target.value }))
                  }
                  placeholder="Enter description"
                  rows={4}
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '2px solid #000',
                    borderRadius: '4px',
                    fontSize: '1rem',
                    resize: 'vertical',
                    boxSizing: 'border-box',
                  }}
                />
              </div>

              <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
                <button
                  type="submit"
                  disabled={creating}
                  style={{
                    ...blackButtonStyle,
                    opacity: creating ? 0.7 : 1,
                  }}
                >
                  {creating ? 'Creating...' : 'Create Community'}
                </button>

                <button
                  type="button"
                  onClick={() => setShowCreateForm(false)}
                  style={buttonStyle}
                >
                  Cancel
                </button>
              </div>
            </div>
          </form>
        </div>
      )}

      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
          gap: '20px',
        }}
      >
        {communities.length === 0 ? (
          <div
            style={{
              gridColumn: '1 / -1',
              textAlign: 'center',
              padding: '3rem',
              backgroundColor: emptyBg,
              border: `2px solid ${emptyBorder}`,
              borderRadius: '4px',
            }}
          >
            <p style={{ fontSize: '1.1rem', color: emptyText, margin: 0 }}>
              No communities found.
            </p>
          </div>
        ) : (
          communities.map((community) => {
            const isJoined = joinedCommunities.includes(community.id);
            const foodCount = getCommunityFoodCount(community.name);

            return (
              <div
                key={community.id}
                style={{
                  backgroundColor: '#fff',
                  border: '4px solid #000',
                  borderRadius: '4px',
                  padding: '1.5rem',
                  boxShadow: '6px 6px 0px rgba(45,27,0,1)',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '1rem',
                  cursor: 'pointer',
                  transition: 'all 0.2s',
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.transform = 'translate(-2px, -2px)';
                  e.currentTarget.style.boxShadow = '8px 8px 0px rgba(45,27,0,1)';
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.transform = 'translate(0, 0)';
                  e.currentTarget.style.boxShadow = '6px 6px 0px rgba(45,27,0,1)';
                }}
                onClick={() => openCommunity(community)}
              >
                <div
                  style={{
                    display: 'flex',
                    alignItems: 'flex-start',
                    justifyContent: 'space-between',
                  }}
                >
                  <h3 style={{ fontSize: '1.25rem', fontWeight: 'bold', margin: 0 }}>
                    {community.name}
                  </h3>

                  {isJoined && (
                    <span
                      style={{
                        backgroundColor: '#000',
                        color: '#fff',
                        fontSize: '0.7rem',
                        fontWeight: 'bold',
                        padding: '0.25rem 0.5rem',
                        borderRadius: '4px',
                        flexShrink: 0,
                        marginLeft: '0.5rem',
                      }}
                    >
                      JOINED
                    </span>
                  )}
                </div>

                <p style={{ color: '#666', margin: 0, fontSize: '0.9rem' }}>
                  {community.description || 'No description available.'}
                </p>

                <div
                  style={{
                    display: 'flex',
                    gap: '1rem',
                    fontSize: '0.85rem',
                    color: '#666',
                    alignItems: 'center',
                    flexWrap: 'wrap',
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                    <MapPin size={14} />
                    {community.location || 'No location'}
                  </div>

                  <div>
                    {foodCount} food {foodCount === 1 ? 'post' : 'posts'}
                  </div>
                </div>

                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleJoinLeave(community.id);
                  }}
                  style={{
                    width: '100%',
                    padding: '0.6rem 1rem',
                    backgroundColor: isJoined ? '#fff' : '#000',
                    color: isJoined ? '#000' : '#fff',
                    fontWeight: 'bold',
                    border: '2px solid #000',
                    borderRadius: '4px',
                    boxShadow: '3px 3px 0px rgba(45,27,0,1)',
                    cursor: 'pointer',
                    marginTop: 'auto',
                  }}
                >
                  {isJoined ? 'Leave' : 'Join'}
                </button>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
}

export default Communities;


import React from 'react';
import { Calendar, MapPin, User, AlertCircle } from 'lucide-react';

function FoodCard({ item = {}, onReserve = () => {} }) {
  const {
    name = 'Food Item',
    description = 'No description',
    imageUrl = 'https://via.placeholder.com/300x200',
    quantity = '0',
    unit = 'pieces',
    expiryDate = '2025-12-31',
    donor = 'Anonymous',
    community = 'Local Community',
    allergens = [],
    reserved = false,
  } = item;

  const getDaysUntilExpiry = () => {
    const today = new Date();
    const expiry = new Date(expiryDate);
    const diffTime = expiry.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  };

  const daysLeft = getDaysUntilExpiry();
  const isExpiringSoon = daysLeft >= 0 && daysLeft <= 2;

  return (
    <div
      style={{
        backgroundColor: '#fff',
        border: '4px solid #000',
        borderRadius: '0',
        boxShadow: '6px 6px 0px rgba(45,27,0,1)',
        overflow: 'hidden',
        transition: 'all 0.2s',
        cursor: 'pointer',
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.boxShadow = '8px 8px 0px rgba(45,27,0,1)';
        e.currentTarget.style.transform = 'translate(-2px, -2px)';
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.boxShadow = '6px 6px 0px rgba(45,27,0,1)';
        e.currentTarget.style.transform = 'translate(0, 0)';
      }}
    >
      {/* Image */}
      <div
        style={{
          position: 'relative',
          height: '200px',
          backgroundColor: '#f0f0f0',
          borderBottom: '4px solid #000',
          overflow: 'hidden',
        }}
      >
        <img
          src={imageUrl}
          alt={name}
          style={{
            width: '100%',
            height: '100%',
            objectFit: 'cover',
          }}
        />
        {/* Badges */}
        {isExpiringSoon && !reserved && (
          <div
            style={{
              position: 'absolute',
              top: '8px',
              right: '8px',
              backgroundColor: '#d32f2f',
              color: '#fff',
              padding: '0.5rem 0.75rem',
              border: '2px solid #000',
              boxShadow: '3px 3px 0px rgba(45,27,0,1)',
              fontWeight: 'bold',
              fontSize: '0.75rem',
            }}
          >
            EXPIRES SOON!
          </div>
        )}
        {reserved && (
          <div
            style={{
              position: 'absolute',
              top: '8px',
              right: '8px',
              backgroundColor: '#ffa500',
              color: '#000',
              padding: '0.5rem 0.75rem',
              border: '2px solid #000',
              boxShadow: '3px 3px 0px rgba(45,27,0,1)',
              fontWeight: 'bold',
              fontSize: '0.75rem',
            }}
          >
            RESERVED
          </div>
        )}
      </div>

      {/* Content */}
      <div style={{ padding: '1rem', display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
        {/* Title & Description */}
        <div>
          <h3 style={{ fontSize: '1.25rem', fontWeight: 'bold', margin: '0 0 0.5rem 0' }}>
            {name}
          </h3>
          <p style={{ fontSize: '0.9rem', color: '#666', margin: 0 }}>
            {description}
          </p>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          {/* Quantity */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <div
              style={{
                backgroundColor: '#ffa500',
                border: '2px solid #000',
                padding: '0.25rem 0.5rem',
                boxShadow: '2px 2px 0px rgba(45,27,0,1)',
                fontWeight: 'bold',
              }}
            >
              {quantity} {unit}
            </div>
          </div>

          {/* Expiry Date */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#666', fontSize: '0.9rem' }}>
            <Calendar size={16} />
            <span>Expires: {new Date(expiryDate).toLocaleDateString()}</span>
          </div>

          {/* Donor */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#666', fontSize: '0.9rem' }}>
            <User size={16} />
            <span>{donor}</span>
          </div>

          {/* Community */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#666', fontSize: '0.9rem' }}>
            <MapPin size={16} />
            <span>{community}</span>
          </div>
        </div>

        {/* Allergys */}
        {allergens.length > 0 && (
          <div
            style={{
              display: 'flex',
              alignItems: 'flex-start',
              gap: '0.5rem',
              backgroundColor: '#ffebee',
              border: '2px solid #d32f2f',
              padding: '0.5rem',
            }}
          >
            <AlertCircle size={16} style={{ color: '#d32f2f', marginTop: '0.25rem', flexShrink: 0 }} />
            <div style={{ fontSize: '0.85rem' }}>
              <span style={{ fontWeight: 'bold', color: '#d32f2f' }}>Allergens: </span>
              <span style={{ color: '#d32f2f' }}>{allergens.join(', ')}</span>
            </div>
          </div>
        )}

        {/* Reserve Button */}
        <button
          onClick={() => onReserve(item)}
          disabled={reserved}
          style={{
            width: '100%',
            padding: '0.75rem 1rem',
            backgroundColor: reserved ? '#ccc' : '#000',
            color: reserved ? '#666' : '#fff',
            fontWeight: 'bold',
            border: '4px solid #000',
            borderRadius: '0',
            boxShadow: '4px 4px 0px rgba(45,27,0,1)',
            cursor: reserved ? 'not-allowed' : 'pointer',
            transition: 'all 0.2s',
            opacity: reserved ? 0.5 : 1,
          }}
          onMouseEnter={(e) => {
            if (!reserved) {
              e.target.style.boxShadow = '6px 6px 0px rgba(45,27,0,1)';
              e.target.style.transform = 'translate(-2px, -2px)';
            }
          }}
          onMouseLeave={(e) => {
            if (!reserved) {
              e.target.style.boxShadow = '4px 4px 0px rgba(45,27,0,1)';
              e.target.style.transform = 'translate(0, 0)';
            }
          }}
        >
          {reserved ? 'ALREADY RESERVED' : 'RESERVE THIS ITEM'}
        </button>
      </div>
    </div>
  );
}

export default FoodCard;
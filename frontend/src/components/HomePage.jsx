import React from 'react';
import FoodCard from './FoodCard';

function HomePage({
                      foodItems = [],
                      joinedCommunities = [],
                      communityFoodItems = [],
                      onReserve = () => {},
                      onViewChange = () => {},
                      a11y = {},
                      currentUser = "", // ← logged-in username
                  }) {
    const isDark = a11y.darkMode;
    const textPrimary = isDark ? '#f0f0f0' : '#000';
    const textMuted = isDark ? '#aaa' : '#666';
    const emptyBg = isDark ? '#1e1e1e' : '#f5f5f5';
    const emptyBorder = isDark ? '#888' : '#000';
    const cardBg = isDark ? '#1e1e1e' : '#fff';
    const borderColor = isDark ? '#888' : '#000';
    const shadow = isDark
        ? '4px 4px 0px rgba(255,255,255,0.08)'
        : '4px 4px 0px rgba(45,27,0,1)';

    // Show posts where donor matches current user or postedByMe flag is true
    const myPosts = foodItems.filter(
        (item) => item.donor === currentUser || item.postedByMe
    );

    const sectionHeadingStyle = {
        fontSize: '1.8rem',
        fontWeight: 'bold',
        marginBottom: '1.5rem',
        paddingBottom: '0.5rem',
        borderBottom: `3px solid ${borderColor}`,
        color: textPrimary,
    };

    const emptyStateStyle = {
        backgroundColor: emptyBg,
        border: `2px solid ${emptyBorder}`,
        borderRadius: '4px',
        padding: '2rem',
        textAlign: 'center',
        color: textMuted,
        minHeight: '150px',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '1rem',
    };

    return (
        <div style={{ padding: '2rem 1rem', maxWidth: '1280px', margin: '0 auto' }}>
            {/* My Posts */}
            <section style={{ marginBottom: '3rem' }}>
                <h2 style={sectionHeadingStyle}>My Posts</h2>
                {myPosts.length === 0 ? (
                    <div style={emptyStateStyle}>
                        <p style={{ margin: 0, color: textMuted }}>
                            You haven't posted any food items yet.
                        </p>
                        <button
                            onClick={() => onViewChange('post')}
                            style={{
                                padding: '0.6rem 1.25rem',
                                backgroundColor: isDark ? '#fff' : '#000',
                                color: isDark ? '#000' : '#fff',
                                fontWeight: 'bold',
                                border: `2px solid ${borderColor}`,
                                borderRadius: '4px',
                                boxShadow: shadow,
                                cursor: 'pointer',
                            }}
                        >
                            Post Food
                        </button>
                    </div>
                ) : (
                    <div
                        style={{
                            display: 'grid',
                            gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))',
                            gap: '20px',
                        }}
                    >
                        {myPosts.map((item) => (
                            <FoodCard key={item.id} item={item} onReserve={onReserve} />
                        ))}
                    </div>
                )}
            </section>

            {/* My Communities */}
            <section style={{ marginBottom: '3rem' }}>
                <h2 style={sectionHeadingStyle}>My Communities</h2>
                {joinedCommunities.length === 0 ? (
                    <div style={emptyStateStyle}>
                        <p style={{ margin: 0, color: textMuted }}>
                            You haven't joined any communities yet.
                        </p>
                        <button
                            onClick={() => onViewChange('communities')}
                            style={{
                                padding: '0.6rem 1.25rem',
                                backgroundColor: isDark ? '#fff' : '#000',
                                color: isDark ? '#000' : '#fff',
                                fontWeight: 'bold',
                                border: `2px solid ${borderColor}`,
                                borderRadius: '4px',
                                boxShadow: shadow,
                                cursor: 'pointer',
                            }}
                        >
                            Browse Communities
                        </button>
                    </div>
                ) : (
                    <div
                        style={{
                            display: 'flex',
                            flexWrap: 'wrap',
                            gap: '0.75rem',
                        }}
                    >
                        {joinedCommunities.map((community) => (
                            <div
                                key={community.id}
                                style={{
                                    backgroundColor: cardBg,
                                    border: `3px solid ${borderColor}`,
                                    borderRadius: '4px',
                                    padding: '0.75rem 1.25rem',
                                    boxShadow: shadow,
                                    color: textPrimary,
                                    fontWeight: 'bold',
                                }}
                            >
                                {community.name}
                            </div>
                        ))}
                    </div>
                )}
            </section>

            {/* Community Posts */}
            <section style={{ marginBottom: '3rem' }}>
                <h2 style={sectionHeadingStyle}>Community Posts</h2>
                {joinedCommunities.length === 0 ? (
                    <div style={emptyStateStyle}>
                        <p style={{ margin: 0, color: textMuted }}>
                            Join a community to see their food posts here.
                        </p>
                    </div>
                ) : communityFoodItems.length === 0 ? (
                    <div style={emptyStateStyle}>
                        <p style={{ margin: 0, color: textMuted }}>
                            Your communities haven't posted any food items yet.
                        </p>
                    </div>
                ) : (
                    <div
                        style={{
                            display: 'grid',
                            gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
                            gap: '20px',
                        }}
                    >
                        {communityFoodItems.map((item) => (
                            <FoodCard key={item.id} item={item} onReserve={onReserve} />
                        ))}
                    </div>
                )}
            </section>
        </div>
    );
}

export default HomePage;
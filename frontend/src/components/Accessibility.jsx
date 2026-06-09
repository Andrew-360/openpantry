import React from 'react';
import { Moon, Type, Contrast, Eye } from 'lucide-react';

const settingsItems = [
    {
        key: 'darkMode',
        label: 'Dark Mode',
        description: 'Switches the app to a dark colour scheme, reducing eye strain in low-light environments.',
        icon: Moon,
    },
    {
        key: 'largeText',
        label: 'Large Text',
        description: 'Increases the font size across the entire app to make text easier to read.',
        icon: Type,
    },
    {
        key: 'highContrast',
        label: 'High Contrast',
        description: 'Boosts the contrast of colours across the app to improve visibility for users with visual impairments.',
        icon: Contrast,
    },
    {
        key: 'ariaLabels',
        label: 'Screen Reader Support',
        description: 'Adds enhanced ARIA labels to interactive elements so screen readers can better describe the page.',
        icon: Eye,
    },
];

function Accessibility({ a11y = {}, onA11yChange = () => {} }) {
    const isDark = a11y.darkMode;

    // Colours that respond to dark mode
    const cardBg      = isDark ? '#1e1e1e' : '#fff';
    const cardBgActive = isDark ? '#2a2a2a' : '#f5f5f5';
    const borderColor = isDark ? '#888'    : '#000';
    const textPrimary = isDark ? '#f0f0f0' : '#000';
    const textMuted   = isDark ? '#aaa'    : '#666';
    const iconBg      = isDark ? '#333'    : '#f0f0f0';
    const shadow      = isDark
        ? '4px 4px 0px rgba(255,255,255,0.08)'
        : '6px 6px 0px rgba(45,27,0,1)';

    const toggleSetting = (key) => {
        onA11yChange({ ...a11y, [key]: !a11y[key] });
    };

    const handleReset = () => {
        onA11yChange({
            darkMode: false,
            largeText: false,
            highContrast: false,
            ariaLabels: true,
        });
    };

    return (
        <div style={{
            maxWidth: '720px',
            margin: '0 auto',
            padding: '2rem 1rem',
            backgroundColor: isDark ? '#121212' : '#fff',
            minHeight: '100vh',
        }}>

            <div style={{ marginBottom: '2rem' }}>
                <h2 style={{ fontSize: '2rem', fontWeight: 'bold', margin: '0 0 0.5rem 0', color: textPrimary }}>
                    Accessibility
                </h2>
                <p style={{ color: textMuted, margin: 0, fontSize: '0.95rem' }}>
                    Adjust these settings to make OpenPantry easier to use.
                    Changes take effect immediately across the whole app.
                </p>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {settingsItems.map(({ key, label, description, icon: Icon }) => {
                    const isOn = !!a11y[key];

                    return (
                        <div
                            key={key}
                            onClick={() => toggleSetting(key)}
                            style={{
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'space-between',
                                gap: '1.5rem',
                                padding: '1.25rem 1.5rem',
                                backgroundColor: isOn ? cardBgActive : cardBg,
                                border: `4px solid ${borderColor}`,
                                borderRadius: '4px',
                                boxShadow: shadow,
                                cursor: 'pointer',
                                transition: 'background-color 0.2s',
                            }}
                        >
                            <div style={{ display: 'flex', alignItems: 'flex-start', gap: '1rem' }}>
                                <div style={{
                                    width: '40px',
                                    height: '40px',
                                    backgroundColor: isOn ? '#000' : iconBg,
                                    border: `2px solid ${borderColor}`,
                                    borderRadius: '4px',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    flexShrink: 0,
                                    transition: 'background-color 0.2s',
                                }}>
                                    <Icon
                                        size={20}
                                        color={isOn ? '#fff' : textPrimary}
                                    />
                                </div>
                                <div>
                                    <div style={{ fontWeight: 'bold', fontSize: '1rem', marginBottom: '0.3rem', color: textPrimary }}>
                                        {label}
                                    </div>
                                    <div style={{ fontSize: '0.85rem', color: textMuted, lineHeight: '1.5' }}>
                                        {description}
                                    </div>
                                </div>
                            </div>

                            <div style={{
                                width: '52px',
                                height: '28px',
                                borderRadius: '14px',
                                backgroundColor: isOn ? (isDark ? '#fff' : '#000') : (isDark ? '#444' : '#ccc'),
                                border: `2px solid ${borderColor}`,
                                boxSizing: 'border-box',
                                position: 'relative',
                                flexShrink: 0,
                                transition: 'background-color 0.2s',
                            }}>
                                <div style={{
                                    position: 'absolute',
                                    top: '50%',
                                    transform: 'translateY(-50%)',
                                    left: isOn ? 'calc(100% - 22px)' : '4px',
                                    width: '18px',
                                    height: '18px',
                                    borderRadius: '50%',
                                    backgroundColor: isOn ? (isDark ? '#000' : '#fff') : (isDark ? '#888' : '#fff'),
                                    transition: 'left 0.2s',
                                    boxShadow: '0 1px 3px rgba(0,0,0,0.3)',
                                }} />
                            </div>
                        </div>
                    );
                })}
            </div>

            <div style={{ marginTop: '2rem' }}>
                <button
                    onClick={handleReset}
                    style={{
                        padding: '0.75rem 1.5rem',
                        backgroundColor: 'transparent',
                        color: textPrimary,
                        fontWeight: 'bold',
                        border: `4px solid ${borderColor}`,
                        borderRadius: '4px',
                        boxShadow: shadow,
                        cursor: 'pointer',
                        fontSize: '0.9rem',
                        transition: 'all 0.2s',
                    }}
                    onMouseEnter={(e) => {
                        e.currentTarget.style.transform = 'translate(-2px, -2px)';
                    }}
                    onMouseLeave={(e) => {
                        e.currentTarget.style.transform = 'translate(0, 0)';
                    }}
                >
                    Reset to Defaults
                </button>
            </div>
        </div>
    );
}

export default Accessibility;
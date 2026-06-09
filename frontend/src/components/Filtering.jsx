import React from 'react';
import { X, Search } from 'lucide-react';

const allergenOptions = [
    'Wheat',
    'Dairy',
    'Eggs',
    'Nuts',
    'Fish',
    'Soy'
];

function Filtering({
                       searchQuery = '',
                       onSearchChange = () => {},
                       selectedAllergens = [],
                       onAllergenToggle = () => {},
                       expiryFilter = 'all',
                       onExpiryFilterChange = () => {},
                       onClearFilters = () => {}
                   }) {
    const hasActiveFilters =
        searchQuery !== '' ||
        selectedAllergens.length > 0 ||
        expiryFilter !== 'all';

    return (
        <div
            style={{
                backgroundColor: '#fff',
                borderBottom: '4px solid #000',
                position: 'sticky',
                top: '88px',
                zIndex: 10,
                boxShadow: '0px 4px 0px rgba(0,0,0,0.2)',
            }}
        >
            <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '1rem' }}>
                <div
                    style={{
                        display: 'flex',
                        flexDirection: 'column',
                        gap: '1rem',
                    }}
                >
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
                        gap: '1rem',
                    }}>

                        <div>
                            <label
                                htmlFor="search"
                                style={{
                                    fontWeight: 'bold',
                                    display: 'block',
                                    marginBottom: '0.5rem',
                                }}
                            >
                                SEARCH
                            </label>
                            <div style={{ position: 'relative' }}>
                                <Search
                                    size={16}
                                    style={{
                                        position: 'absolute',
                                        left: '0.75rem',
                                        top: '50%',
                                        transform: 'translateY(-50%)',
                                        color: '#666',
                                        pointerEvents: 'none',
                                    }}
                                />
                                <input
                                    id="search"
                                    type="text"
                                    value={searchQuery}
                                    onChange={(e) => onSearchChange(e.target.value)}
                                    placeholder="Search by name or description..."
                                    style={{
                                        width: '100%',
                                        padding: '0.5rem 1rem 0.5rem 2.25rem',
                                        border: '2px solid #000',
                                        boxShadow: '3px 3px 0px rgba(0,0,0,0.2)',
                                        backgroundColor: '#fff',
                                        color: '#000',
                                        fontFamily: 'inherit',
                                        fontSize: '1rem',
                                        boxSizing: 'border-box',
                                    }}
                                />
                            </div>
                        </div>

                        <div>
                            <label
                                htmlFor="expiry"
                                style={{
                                    fontWeight: 'bold',
                                    display: 'block',
                                    marginBottom: '0.5rem',
                                }}
                            >
                                EXPIRY DATE
                            </label>
                            <select
                                id="expiry"
                                value={expiryFilter}
                                onChange={(e) => onExpiryFilterChange(e.target.value)}
                                style={{
                                    padding: '0.5rem 1rem',
                                    border: '2px solid #000',
                                    width: '100%',
                                    boxShadow: '3px 3px 0px rgba(0,0,0,0.2)',
                                    backgroundColor: '#fff',
                                    color: '#000',
                                    cursor: 'pointer',
                                    boxSizing: 'border-box',
                                }}
                            >
                                <option value="all">All Items</option>
                                <option value="today">Expiring Today</option>
                                <option value="3days">Within the next 3 Days</option>
                                <option value="week">Within the next Week</option>
                            </select>
                        </div>
                    </div>

                    <div>
                        <div
                            style={{
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'space-between',
                                marginBottom: '0.5rem',
                            }}
                        >
                            <label style={{ fontWeight: 'bold' }}>
                                EXCLUDE ALLERGENS
                            </label>
                            {hasActiveFilters && (
                                <button
                                    onClick={onClearFilters}
                                    style={{
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: '0.5rem',
                                        padding: '0.5rem 1rem',
                                        border: '2px solid #000',
                                        boxShadow: '3px 3px 0px rgba(0,0,0,0.2)',
                                        cursor: 'pointer',
                                        fontSize: '0.75rem',
                                        backgroundColor: '#000',
                                        color: '#fff',
                                        fontWeight: 'bold',
                                    }}
                                >
                                    <X size={12} />
                                    Clear All
                                </button>
                            )}
                        </div>
                        <div
                            style={{
                                display: 'flex',
                                flexWrap: 'wrap',
                                gap: '0.5rem',
                            }}
                        >
                            {allergenOptions.map((allergen) => (
                                <label
                                    key={allergen}
                                    style={{
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: '0.5rem',
                                        backgroundColor: selectedAllergens.includes(allergen) ? '#000' : '#fff',
                                        color: selectedAllergens.includes(allergen) ? '#fff' : '#000',
                                        border: '2px solid #000',
                                        padding: '0.5rem',
                                        boxShadow: '3px 3px 0px rgba(0,0,0,0.2)',
                                        cursor: 'pointer',
                                    }}
                                >
                                    <input
                                        type="checkbox"
                                        checked={selectedAllergens.includes(allergen)}
                                        onChange={() => onAllergenToggle(allergen)}
                                    />
                                    <span>{allergen}</span>
                                </label>
                            ))}
                        </div>
                    </div>

                </div>
            </div>
        </div>
    );
}

export default Filtering;
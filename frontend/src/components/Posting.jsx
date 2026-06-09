import React, { useState } from 'react';
import { createFoodItem, getCommunityByName } from "../api/FoodPostingApi";
// adjust path if needed

const allergenOptions = [
    'Wheat',
    'Dairy',
    'Eggs',
    'Nuts',
    'Fish',
    'Soy'
];

function Posting({ onFoodPosted = () => {}, onViewChange = () => {}, prefilledCommunity = '', onUnmount = () => {} }) {
    const [submitted, setSubmitted] = useState(false);
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        quantity: '',
        unit: '',
        expiryDate: '',
        ingredients: '',
        imageUrl: '',
        community: prefilledCommunity,
        communityId: null,
        allergens: [],
    });

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!formData.community.trim()) {
            alert("Please enter a community.");
            return;
        }

        try {
            // 1️⃣ Resolve community by name
            const communities = await getCommunityByName(formData.community.trim());
            if (!communities.length) {
                alert("Community not found. Please enter a valid community.");
                return;
            }

            // 2️⃣ Pick the first matching community
            const selectedCommunity = communities[0];
            const communityId = selectedCommunity.id;

            // 3️⃣ Prepare food data
            const foodData = {
                name: formData.name,
                description: formData.description,
                quantity: parseFloat(formData.quantity),
                unit: formData.unit,
                expiry: formData.expiryDate,
                ingredients: formData.ingredients
                    .split(',')
                    .map((i) => i.trim())
                    .filter((i) => i),
                image: formData.imageUrl || 'https://via.placeholder.com/300x200',

                community: selectedCommunity.name, // ✅ added for frontend filtering
                communityId, // ✅ ensure backend receives correct ID

                tagIds: [],  // map allergens to tags if needed
                userProfileId: parseInt(localStorage.getItem("userId")), // adjust if needed
            };

            // 4️⃣ Post the food item
            await createFoodItem(foodData);

            // 5️⃣ Reset form
            setFormData({
                name: '',
                description: '',
                quantity: '',
                unit: '',
                expiryDate: '',
                ingredients: '',
                imageUrl: '',
                community: formData.community, // keep prefilled community
                communityId: communityId, // ✅ keep correct ID
                allergens: [],
            });

            setSubmitted(true);

            onFoodPosted(foodData);
        } catch (error) {
            console.error(error);
            alert(error.message || "Failed to post food item");
        }
    };

    const handleAllergenToggle = (allergen) => {
        setFormData((prev) => ({
            ...prev,
            allergens: prev.allergens.includes(allergen)
                ? prev.allergens.filter((a) => a !== allergen)
                : [...prev.allergens, allergen],
        }));
    };

    if (submitted) {
        return (
            <div style={{ padding: '2rem', textAlign: 'center' }}>
                <h2 style={{ fontSize: '2rem', fontWeight: 'bold', marginBottom: '1rem' }}>
                    Food Posted Successfully!
                </h2>
                <p style={{ color: '#666', marginBottom: '2rem' }}>
                    Your item is now visible to your community. Thank you for sharing!
                </p>
                <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center' }}>
                    <button
                        onClick={() => {
                            setSubmitted(false);
                        }}
                        style={{
                            padding: '0.75rem 1.5rem',
                            backgroundColor: '#fff',
                            color: '#000',
                            fontWeight: 'bold',
                            border: '3px solid #000',
                            borderRadius: '4px',
                            boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                            cursor: 'pointer',
                        }}
                    >
                        Post Another Item
                    </button>
                    <button
                        onClick={() => onViewChange('browse')}
                        style={{
                            padding: '0.75rem 1.5rem',
                            backgroundColor: '#000',
                            color: '#fff',
                            fontWeight: 'bold',
                            border: '3px solid #000',
                            borderRadius: '4px',
                            boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                            cursor: 'pointer',
                        }}
                    >
                        Back to Browse
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div style={{ maxWidth: '600px', margin: '2rem auto', padding: '0 1rem' }}>
            <div
                style={{
                    backgroundColor: '#fff',
                    borderRadius: '8px',
                    border: '4px solid #000',
                    boxShadow: '12px 12px 0px rgba(45,27,0,1)',
                    padding: '2rem',
                }}
            >
                <h2 style={{ fontSize: '1.5rem', fontWeight: 'bold', marginBottom: '0.5rem' }}>
                    POST FOOD ITEM
                </h2>
                <p style={{ fontSize: '0.9rem', color: '#666', marginBottom: '1.5rem' }}>
                    Fill in the details below to share food with your community.
                </p>

                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>

                    <div>
                        <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.5rem' }}>
                            FOOD NAME *
                        </label>
                        <input
                            type="text"
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                            required
                            style={{
                                width: '100%',
                                padding: '0.75rem',
                                border: '4px solid #000',
                                borderRadius: '4px',
                                boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                                fontFamily: 'inherit',
                                boxSizing: 'border-box',
                            }}
                        />
                    </div>

                    <div>
                        <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.5rem' }}>
                            DESCRIPTION *
                        </label>
                        <textarea
                            value={formData.description}
                            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                            required
                            rows={3}
                            style={{
                                width: '100%',
                                padding: '0.75rem',
                                border: '4px solid #000',
                                borderRadius: '4px',
                                boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                                fontFamily: 'inherit',
                                resize: 'vertical',
                                boxSizing: 'border-box',
                            }}
                        />
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                        <div>
                            <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.5rem' }}>
                                QUANTITY *
                            </label>
                            <input
                                type="number"
                                step="0.01"
                                value={formData.quantity}
                                onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
                                required
                                style={{
                                    width: '100%',
                                    padding: '0.75rem',
                                    border: '4px solid #000',
                                    borderRadius: '4px',
                                    boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                                    fontFamily: 'inherit',
                                    boxSizing: 'border-box',
                                }}
                            />
                        </div>
                        <div>
                            <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.5rem' }}>
                                UNIT *
                            </label>
                            <input
                                type="text"
                                placeholder="e.g. kg, loaf, cans"
                                value={formData.unit}
                                onChange={(e) => setFormData({ ...formData, unit: e.target.value })}
                                required
                                style={{
                                    width: '100%',
                                    padding: '0.75rem',
                                    border: '4px solid #000',
                                    borderRadius: '4px',
                                    boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                                    fontFamily: 'inherit',
                                    boxSizing: 'border-box',
                                }}
                            />
                        </div>
                    </div>

                    <div>
                        <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.5rem' }}>
                            EXPIRY DATE *
                        </label>
                        <input
                            type="date"
                            value={formData.expiryDate}
                            onChange={(e) => setFormData({ ...formData, expiryDate: e.target.value })}
                            required
                            min={new Date().toISOString().split('T')[0]}
                            style={{
                                width: '100%',
                                padding: '0.75rem',
                                border: '4px solid #000',
                                borderRadius: '4px',
                                boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                                fontFamily: 'inherit',
                                boxSizing: 'border-box',
                            }}
                        />
                    </div>

                    <div>
                        <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.5rem' }}>
                            IMAGE URL
                        </label>
                        <input
                            type="url"
                            placeholder="https://... (optional)"
                            value={formData.imageUrl}
                            onChange={(e) => setFormData({ ...formData, imageUrl: e.target.value })}
                            style={{
                                width: '100%',
                                padding: '0.75rem',
                                border: '4px solid #000',
                                borderRadius: '4px',
                                boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                                fontFamily: 'inherit',
                                boxSizing: 'border-box',
                            }}
                        />
                    </div>

                    <div>
                        <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.5rem' }}>
                            COMMUNITY
                        </label>
                        <input
                            type="text"
                            placeholder="e.g. East Kilbride"
                            value={formData.community}
                            onChange={(e) => setFormData({ ...formData, community: e.target.value })}
                            style={{
                                width: '100%',
                                padding: '0.75rem',
                                border: '4px solid #000',
                                borderRadius: '4px',
                                boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                                fontFamily: 'inherit',
                                boxSizing: 'border-box',
                                backgroundColor: prefilledCommunity ? '#f5f5f5' : '#fff',
                            }}
                        />
                        {prefilledCommunity && (
                            <p style={{ fontSize: '0.8rem', color: '#666', margin: '0.4rem 0 0 0' }}>
                                Pre-filled from your community. You can change this if needed.
                            </p>
                        )}
                    </div>

                    <div>
                        <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.5rem' }}>
                            INGREDIENTS
                        </label>
                        <input
                            type="text"
                            placeholder="e.g. flour, sugar, butter (comma separated)"
                            value={formData.ingredients}
                            onChange={(e) => setFormData({ ...formData, ingredients: e.target.value })}
                            style={{
                                width: '100%',
                                padding: '0.75rem',
                                border: '4px solid #000',
                                borderRadius: '4px',
                                boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                                fontFamily: 'inherit',
                                boxSizing: 'border-box',
                            }}
                        />
                    </div>

                    <div>
                        <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '0.75rem' }}>
                            ALLERGENS
                        </label>
                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                            {allergenOptions.map((allergen) => (
                                <label
                                    key={allergen}
                                    style={{
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: '0.5rem',
                                        backgroundColor: formData.allergens.includes(allergen) ? '#000' : '#fff',
                                        color: formData.allergens.includes(allergen) ? '#fff' : '#000',
                                        border: '2px solid #000',
                                        padding: '0.5rem',
                                        borderRadius: '4px',
                                        boxShadow: '2px 2px 0px rgba(45,27,0,1)',
                                        cursor: 'pointer',
                                    }}
                                >
                                    <input
                                        type="checkbox"
                                        checked={formData.allergens.includes(allergen)}
                                        onChange={() => handleAllergenToggle(allergen)}
                                    />
                                    <span>{allergen}</span>
                                </label>
                            ))}
                        </div>
                    </div>

                    <div style={{ display: 'flex', gap: '1rem', paddingTop: '1rem' }}>
                        <button
                            type="submit"
                            style={{
                                flex: 1,
                                padding: '0.75rem 1rem',
                                backgroundColor: '#000',
                                color: '#fff',
                                fontWeight: 'bold',
                                border: '4px solid #000',
                                borderRadius: '4px',
                                boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                                cursor: 'pointer',
                                transition: 'all 0.2s',
                            }}
                            onMouseEnter={(e) => {
                                e.target.style.boxShadow = '6px 6px 0px rgba(45,27,0,1)';
                                e.target.style.transform = 'translate(-2px, -2px)';
                            }}
                            onMouseLeave={(e) => {
                                e.target.style.boxShadow = '4px 4px 0px rgba(45,27,0,1)';
                                e.target.style.transform = 'translate(0, 0)';
                            }}
                        >
                            POST ITEM
                        </button>
                        <button
                            type="button"
                            onClick={() => onViewChange('browse')}
                            style={{
                                padding: '0.75rem 1rem',
                                backgroundColor: '#fff',
                                color: '#000',
                                fontWeight: 'bold',
                                border: '4px solid #000',
                                borderRadius: '4px',
                                boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                                cursor: 'pointer',
                                transition: 'all 0.2s',
                            }}
                            onMouseEnter={(e) => {
                                e.target.style.boxShadow = '6px 6px 0px rgba(45,27,0,1)';
                                e.target.style.transform = 'translate(-2px, -2px)';
                            }}
                            onMouseLeave={(e) => {
                                e.target.style.boxShadow = '4px 4px 0px rgba(45,27,0,1)';
                                e.target.style.transform = 'translate(0, 0)';
                            }}
                        >
                            CANCEL
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default Posting;
import React, { useState, useEffect } from 'react';
import { Calendar, MapPin, User, Trash2 } from 'lucide-react';
import {
    createReservation,
    cancelReservation,
    getReservationsByUser
} from '../api/ReservationApi'; // adjust path if needed

// ---------------- Reserve Dialog Component ----------------
function ReserveDialog({ open = false, item = null, onClose = () => {}, onConfirm = () => {} }) {
    if (!open || !item) return null;

    const handleConfirm = () => {
        onConfirm(item);
        onClose();
    };

    return (
        <div
            style={{
                position: 'fixed',
                inset: 0,
                backgroundColor: 'rgba(0,0,0,0.5)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                zIndex: 50,
            }}
            onClick={onClose}
        >
            <div
                style={{
                    backgroundColor: '#fff',
                    borderRadius: '8px',
                    border: '4px solid #000',
                    boxShadow: '12px 12px 0px rgba(45,27,0,1)',
                    maxWidth: '500px',
                    width: '90%',
                    padding: '2rem',
                }}
                onClick={(e) => e.stopPropagation()}
            >
                <h2 style={{ fontSize: '1.5rem', fontWeight: 'bold', marginBottom: '1rem' }}>
                    RESERVE THIS ITEM
                </h2>

                <div style={{ marginBottom: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    <div style={{ fontSize: '0.95rem' }}>Are you sure you want to reserve:</div>

                    <div
                        style={{
                            backgroundColor: '#f5f5f5',
                            border: '2px solid #000',
                            padding: '1rem',
                            display: 'flex',
                            flexDirection: 'column',
                            gap: '0.5rem',
                        }}
                    >
                        <div style={{ fontWeight: 'bold', fontSize: '1.1rem' }}>{item.name}</div>
                        <div>
                            Quantity: {item.quantity} {item.unit}
                        </div>
                        <div>Pickup: {item.community}</div>
                    </div>

                    <div style={{ fontSize: '0.9rem', color: '#666' }}>
                        Once confirmed, the donor will be notified and you can arrange pickup directly with them via your
                        community page.
                    </div>
                </div>

                <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                    <button
                        onClick={onClose}
                        style={{
                            padding: '0.75rem 1.5rem',
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

                    <button
                        onClick={handleConfirm}
                        style={{
                            padding: '0.75rem 1.5rem',
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
                        CONFIRM RESERVATION
                    </button>
                </div>
            </div>
        </div>
    );
}

// ---------------- Cancel Confirmation Dialog ----------------
function CancelDialog({ open = false, itemName = '', onClose = () => {}, onConfirm = () => {} }) {
    if (!open) return null;

    return (
        <div
            style={{
                position: 'fixed',
                inset: 0,
                backgroundColor: 'rgba(0,0,0,0.5)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                zIndex: 51,
            }}
            onClick={onClose}
        >
            <div
                style={{
                    backgroundColor: '#fff',
                    borderRadius: '8px',
                    border: '4px solid #d32f2f',
                    boxShadow: '12px 12px 0px rgba(211, 47, 47, 0.3)',
                    maxWidth: '400px',
                    width: '90%',
                    padding: '2rem',
                }}
                onClick={(e) => e.stopPropagation()}
            >
                <h2 style={{ fontSize: '1.3rem', fontWeight: 'bold', marginBottom: '1rem', color: '#d32f2f' }}>
                    CANCEL RESERVATION
                </h2>

                <div style={{ marginBottom: '1.5rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                    <div style={{ fontSize: '0.95rem' }}>Are you sure you want to cancel the reservation for:</div>
                    <div style={{ fontWeight: 'bold', fontSize: '1rem', color: '#000' }}>{itemName}</div>
                </div>

                <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                    <button
                        onClick={onClose}
                        style={{
                            padding: '0.75rem 1.5rem',
                            backgroundColor: '#fff',
                            color: '#000',
                            fontWeight: 'bold',
                            border: '4px solid #000',
                            borderRadius: '4px',
                            boxShadow: '4px 4px 0px rgba(45,27,0,1)',
                            cursor: 'pointer',
                            transition: 'all 0.2s',
                        }}
                    >
                        KEEP RESERVATION
                    </button>

                    <button
                        onClick={onConfirm}
                        style={{
                            padding: '0.75rem 1.5rem',
                            backgroundColor: '#d32f2f',
                            color: '#fff',
                            fontWeight: 'bold',
                            border: '4px solid #d32f2f',
                            borderRadius: '4px',
                            boxShadow: '4px 4px 0px rgba(211, 47, 47, 0.3)',
                            cursor: 'pointer',
                            transition: 'all 0.2s',
                        }}
                    >
                        CANCEL RESERVATION
                    </button>
                </div>
            </div>
        </div>
    );
}

// ---------------- Reservations List Component ----------------
function Reservations({ a11y = {} }) {
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(false);
    const [cancelDialogOpen, setCancelDialogOpen] = useState(false);
    const [reserveDialogOpen, setReserveDialogOpen] = useState(false);
    const [selectedReservation, setSelectedReservation] = useState(null);
    const [selectedItem, setSelectedItem] = useState(null);

    const isDark = a11y.darkMode;
    const emptyBg = isDark ? '#1e1e1e' : '#f5f5f5';
    const emptyBorder = isDark ? '#888' : '#000';
    const emptyText = isDark ? '#aaa' : '#666';
    const headingColor = isDark ? '#f0f0f0' : '#000';

    // ---------------- Fetch reservations ----------------
    useEffect(() => {
        const fetchReservations = async () => {
            setLoading(true);
            try {
                const data = await getReservationsByUser();
                setReservations(data);
            } catch (error) {
                console.error("Error fetching reservations:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchReservations();
    }, []);

    // ---------------- Create reservation ----------------
    const handleCreateReservation = async (item) => {
        try {
            const userProfileId = 1; // replace with real userProfileId
            const response = await createReservation({
                userProfileId,
                foodItemId: item.id,
                quantity: item.quantity || 1
            });
            setReservations((prev) => [...prev, response]);
        } catch (error) {
            console.error("Error creating reservation:", error);
        }
    };

    // ---------------- Cancel reservation ----------------
    const handleCancelClick = (reservation) => {
        setSelectedReservation(reservation);
        setCancelDialogOpen(true);
    };

    const handleConfirmCancel = async () => {
        if (!selectedReservation) return;

        try {
            await cancelReservation(selectedReservation.id);
            setReservations((prev) => prev.filter((res) => res.id !== selectedReservation.id));
        } catch (error) {
            console.error("Error cancelling reservation:", error);
        } finally {
            setCancelDialogOpen(false);
            setSelectedReservation(null);
        }
    };

    // ---------------- Render ----------------
    return (
        <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '2rem 1rem' }}>
            <CancelDialog
                open={cancelDialogOpen}
                itemName={selectedReservation?.name || ''}
                onClose={() => {
                    setCancelDialogOpen(false);
                    setSelectedReservation(null);
                }}
                onConfirm={handleConfirmCancel}
            />

            <ReserveDialog
                open={reserveDialogOpen}
                item={selectedItem}
                onClose={() => setReserveDialogOpen(false)}
                onConfirm={handleCreateReservation}
            />

            <h2 style={{ fontSize: '2rem', fontWeight: 'bold', marginBottom: '1.5rem', color: headingColor }}>
                My Reservations
            </h2>

            {loading ? (
                <p style={{ textAlign: 'center', fontSize: '1.2rem' }}>Loading reservations...</p>
            ) : reservations.length === 0 ? (
                <div
                    style={{
                        textAlign: 'center',
                        padding: '3rem',
                        backgroundColor: emptyBg,
                        border: `2px solid ${emptyBorder}`,
                        borderRadius: '4px',
                    }}
                >
                    <p style={{ fontSize: '1.1rem', color: emptyText }}>
                        No reservations yet. Head to Browse Food to reserve an item!
                    </p>
                </div>
            ) : (
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
                    {reservations.map((item) => (
                        <div
                            key={item.id}
                            style={{
                                backgroundColor: '#fff',
                                border: '4px solid #000',
                                borderRadius: '4px',
                                padding: '1rem',
                                boxShadow: '6px 6px 0px rgba(45,27,0,1)',
                                display: 'flex',
                                flexDirection: 'column',
                            }}
                        >
                            <h3 style={{ fontSize: '1.25rem', fontWeight: 'bold', marginBottom: '0.5rem' }}>{item.foodItemName}</h3>
                            <p style={{ color: '#666', marginBottom: '1rem' }}>Quantity: {item.quantity}</p>

                            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', marginBottom: '1rem', flex: 1 }}>
                                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                    <Calendar size={16} /> Reserved on: {new Date(item.reservationDate).toLocaleDateString()}
                                </div>
                                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                    <User size={16} /> {item.username}
                                </div>
                                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                    <MapPin size={16} /> {item.community}
                                </div>
                                <div
                                    style={{
                                        backgroundColor: '#e8f5e9',
                                        border: '2px solid #4caf50',
                                        padding: '0.75rem',
                                        borderRadius: '4px',
                                        textAlign: 'center',
                                        fontWeight: 'bold',
                                        color: '#2e7d32',
                                        marginTop: '0.5rem',
                                    }}
                                >
                                    Status: {item.reservationStatus}
                                </div>
                            </div>

                            <button
                                onClick={() => handleCancelClick(item)}
                                style={{
                                    width: '100%',
                                    padding: '0.75rem 1rem',
                                    backgroundColor: '#fff',
                                    color: '#d32f2f',
                                    fontWeight: 'bold',
                                    border: '2px solid #d32f2f',
                                    borderRadius: '4px',
                                    cursor: 'pointer',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    gap: '0.5rem',
                                    transition: 'all 0.2s',
                                }}
                                onMouseEnter={(e) => {
                                    e.target.style.backgroundColor = '#ffebee';
                                }}
                                onMouseLeave={(e) => {
                                    e.target.style.backgroundColor = '#fff';
                                }}
                            >
                                <Trash2 size={16} /> Cancel Reservation
                            </button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export { ReserveDialog };
export default Reservations;
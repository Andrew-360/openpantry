// src/api/ReservationApi.js

const API_BASE_URL = "http://localhost:8080/api";

function getAuthHeaders() {
    const token = localStorage.getItem("token");
    return {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
    };
}

export async function createReservation({ userProfileId, foodItemId, quantity = 1 }) {
    const response = await fetch(`${API_BASE_URL}/reservations`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify({ userProfileId, foodItemId, quantity }),
    });
    if (!response.ok) throw new Error("Failed to create reservation");
    return response.json();
}

export async function completeReservation(reservationId) {
    const response = await fetch(`${API_BASE_URL}/reservations/${reservationId}/completion`, {
        method: "PATCH",
        headers: getAuthHeaders(),
    });
    if (!response.ok) throw new Error("Failed to complete reservation");
    return response.json();
}

export async function cancelReservation(reservationId) {
    const response = await fetch(`${API_BASE_URL}/reservations/${reservationId}/cancellation`, {
        method: "PATCH",
        headers: getAuthHeaders(),
    });
    if (!response.ok) throw new Error("Failed to cancel reservation");
    return response.json();
}

export async function getReservationById(id) {
    const response = await fetch(`${API_BASE_URL}/reservations/${id}`, {
        method: "GET",
        headers: getAuthHeaders(),
    });
    if (!response.ok) throw new Error("Failed to fetch reservation by ID");
    return response.json();
}

export async function getReservationsByUser() {
    const response = await fetch(`${API_BASE_URL}/reservations/user`, {
        method: "GET",
        headers: getAuthHeaders(),
    });
    if (!response.ok) throw new Error("Failed to fetch reservations for user");
    return response.json();
}

export async function getReservationsByUserAndStatus(status) {
    const response = await fetch(`${API_BASE_URL}/reservations/user/status/${status}`, {
        method: "GET",
        headers: getAuthHeaders(),
    });
    if (!response.ok) throw new Error("Failed to fetch reservations by user and status");
    return response.json();
}

export async function getReservationsByStatus(status) {
    const response = await fetch(`${API_BASE_URL}/reservations/status/${status}`, {
        method: "GET",
        headers: getAuthHeaders(),
    });
    if (!response.ok) throw new Error("Failed to fetch reservations by status");
    return response.json();
}

export async function getReservationsByFoodItem(foodItemId) {
    const response = await fetch(`${API_BASE_URL}/reservations/food/${foodItemId}`, {
        method: "GET",
        headers: getAuthHeaders(),
    });
    if (!response.ok) throw new Error("Failed to fetch reservations by food item ID");
    return response.json();
}
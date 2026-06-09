const API_BASE_URL = "http://localhost:8080/food-items";

async function handleResponse(response, defaultMessage) {
    const contentType = response.headers.get("content-type") || "";

    let data = null;

    try {
        if (contentType.includes("application/json")) {
            data = await response.json();
        } else {
            data = await response.text();
        }
    } catch (error) {
        data = null;
    }

    if (!response.ok) {
        const errorMessage =
            (typeof data === "object" && data?.message) ||
            (typeof data === "string" && data) ||
            defaultMessage;

        throw new Error(errorMessage);
    }

    return data;
}

function getAuthHeaders() {
    const token = localStorage.getItem("token");

    return {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
    };
}

// GET all food items
export async function getFoodItems() {
    const response = await fetch(API_BASE_URL, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    return handleResponse(response, "Failed to fetch food items");
}

// GET available food items
export async function getAvailableFoodItems() {
    const response = await fetch(`${API_BASE_URL}/available`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    return handleResponse(response, "Failed to fetch available food items");
}

// GET food items by name search
export async function searchFoodItemsByName(name) {
    const params = new URLSearchParams();
    params.append("name", name);

    const response = await fetch(`${API_BASE_URL}/search?${params.toString()}`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    return handleResponse(response, "Failed to search food items");
}

// GET food items by tag ids
export async function getFoodItemsByTags(tagIds = []) {
    const params = new URLSearchParams();

    tagIds.forEach((id) => params.append("tag_ids", id));

    const response = await fetch(`${API_BASE_URL}/tag?${params.toString()}`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    return handleResponse(response, "Failed to fetch food items by tags");
}

// POST create food item
export async function createFoodItem(foodData) {
    const response = await fetch(API_BASE_URL, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(foodData),
    });

    return handleResponse(response, "Failed to create food item");
}

export async function getCommunityByName(name) {
    const params = new URLSearchParams({ name });

    const response = await fetch(`http://localhost:8080/api/communities/search?${params.toString()}`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    return handleResponse(response, "Failed to fetch community by name");
}

// GET reservations for current user
export async function getMyReservations(token) {
    const response = await fetch("http://localhost:8080/reservations", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
    });
    return handleResponse(response, "Failed to fetch reservations");
}

// POST create a reservation
export async function createReservation(foodItemId, userProfileId, quantity, token) {
    const response = await fetch("http://localhost:8080/reservations", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
            foodItemId,
            userProfileId,
            quantity,
        }),
    });
    return handleResponse(response, "Failed to create reservation");
}
const API_BASE_URL = "http://localhost:8080/api/communities";

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

export async function getCommunities(location = "", page = 0, size = 10) {
  const params = new URLSearchParams();

  if (location) {
    params.append("location", location);
  }

  params.append("page", page);
  params.append("size", size);

  const response = await fetch(`${API_BASE_URL}?${params.toString()}`, {
    method: "GET",
    headers: getAuthHeaders(),
  });

  return handleResponse(response, "Failed to fetch communities");
}

export async function getCommunityById(id) {
  const response = await fetch(`${API_BASE_URL}/${id}`, {
    method: "GET",
    headers: getAuthHeaders(),
  });

  return handleResponse(response, "Failed to fetch community");
}

export async function createCommunity(communityData) {
  const response = await fetch(API_BASE_URL, {
    method: "POST",
    headers: getAuthHeaders(),
    body: JSON.stringify(communityData),
  });

  return handleResponse(response, "Failed to create community");
}

export async function updateCommunity(id, communityData) {
  const response = await fetch(`${API_BASE_URL}/${id}`, {
    method: "PUT",
    headers: getAuthHeaders(),
    body: JSON.stringify(communityData),
  });

  return handleResponse(response, "Failed to update community");
}

export async function deleteCommunity(id) {
  const response = await fetch(`${API_BASE_URL}/${id}`, {
    method: "DELETE",
    headers: getAuthHeaders(),
  });

  return handleResponse(response, "Failed to delete community");
}
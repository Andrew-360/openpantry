const API_BASE_URL = "http://localhost:8080/api/auth";

async function handleResponse(response, defaultMessage) {
  const contentType = response.headers.get("content-type") || "";

  let data;
  try {
    if (contentType.includes("application/json")) {
      data = await response.json();
    } else {
      data = await response.text();
    }
  } catch {
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

export async function registerUser(userData) {
  const response = await fetch(`${API_BASE_URL}/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userData),
  });

  return handleResponse(response, "Failed to register user");
}

export async function loginUser(loginData) {
  const response = await fetch(`${API_BASE_URL}/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(loginData),
  });

  return handleResponse(response, "Login failed");
}
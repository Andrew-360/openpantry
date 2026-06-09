const API_BASE_URL = 'http://localhost:8080/api/memberships';

function getAuthHeaders() {
  const token = localStorage.getItem('token');

  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

async function handleResponse(response, defaultMessage) {
  const contentType = response.headers.get('content-type') || '';
  let data = null;

  try {
    if (contentType.includes('application/json')) {
      data = await response.json();
    } else {
      data = await response.text();
    }
  } catch (error) {
    data = null;
  }

  if (!response.ok) {
    const errorMessage =
      (typeof data === 'object' && data?.message) ||
      (typeof data === 'string' && data) ||
      defaultMessage;

    throw new Error(errorMessage);
  }

  return data;
}

export async function joinCommunity(communityId) {
  const response = await fetch(`${API_BASE_URL}/community/${communityId}`, {
    method: 'POST',
    headers: getAuthHeaders(),
  });

  return handleResponse(response, 'Failed to join community');
}

export async function leaveCommunity(communityId) {
  const response = await fetch(`${API_BASE_URL}/community/${communityId}`, {
    method: 'DELETE',
    headers: getAuthHeaders(),
  });

  return handleResponse(response, 'Failed to leave community');
}

export async function getMembershipsByCommunity(communityId) {
  const response = await fetch(`${API_BASE_URL}/community/${communityId}`, {
    method: 'GET',
    headers: getAuthHeaders(),
  });

  return handleResponse(response, 'Failed to load community members');
}
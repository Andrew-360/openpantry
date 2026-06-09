const TAG_API_URL = "http://localhost:8080/tags";

function getAuthHeaders() {
  const token = localStorage.getItem("token");

  return {
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

// GET all tags
export async function getTags() {
  const res = await fetch(TAG_API_URL, {
    headers: getAuthHeaders(),
  });

  return res.json();
}

// CREATE tag (IMPORTANT FIX HERE)
export async function createTag(name) {
  const res = await fetch(`${TAG_API_URL}?name=${encodeURIComponent(name)}`, {
    method: "POST",
    headers: getAuthHeaders(),
  });

  return res.json();
}

// DELETE tag
export async function deleteTag(id) {
  await fetch(`${TAG_API_URL}/${id}`, {
    method: "DELETE",
    headers: getAuthHeaders(),
  });
}
// Simple API utility to get current user info
import { API_BASE_URL } from "./config";
export async function fetchCurrentUser() {
  const res = await fetch(`${API_BASE_URL}/api/user/me`, { credentials: "include" });
  if (!res.ok) throw new Error("Failed to fetch user");
  return res.json();
}

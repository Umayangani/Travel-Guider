// Simple API utility to get current user info
export async function fetchCurrentUser() {
  const res = await fetch("/api/user/me", { credentials: "include" });
  if (!res.ok) throw new Error("Failed to fetch user");
  return res.json();
}

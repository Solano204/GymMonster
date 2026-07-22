import axios, { AxiosError } from "axios";
import type { ApiErrorBody } from "@/types/api";
import { authStore } from "@/lib/auth";

/**
 * In dev, requests to /api/* and /GymMonster/* are proxied to the gateway by
 * Vite (see vite.config.ts). In production, set VITE_API_BASE_URL to the
 * deployed gateway origin (e.g. https://api.monstergym.com) via a .env file.
 */
export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "",
  headers: { "Content-Type": "application/json" },
});

export class ApiError extends Error {
  status?: number;
  body?: ApiErrorBody | string;

  constructor(message: string, status?: number, body?: ApiErrorBody | string) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.body = body;
  }
}

// The gateway's RedisTokenValidationFilter requires both an `Authorization:
// Bearer <jwt>` header and a plain `username` header on every request except
// /GymMonster/auth/login and /GymMonster/auth/logout - attach them here so
// every api/endpoints.ts call doesn't have to do it itself.
http.interceptors.request.use((config) => {
  const token = authStore.getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
    if (!config.headers.username) {
      config.headers.username = authStore.getUsername();
    }
  }
  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiErrorBody | string>) => {
    const status = error.response?.status;
    const body = error.response?.data;
    const message =
      (typeof body === "object" && body?.error) ||
      error.message ||
      "Unexpected network error";
    if (status === 401) {
      authStore.clear();
    }
    return Promise.reject(new ApiError(message, status, body));
  },
);

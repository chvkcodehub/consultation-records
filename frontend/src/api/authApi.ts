import { apiClient } from "./client";
import type { AuthResponse } from "../types";

export const authApi = {
  login: (email: string, password: string) => apiClient.post<AuthResponse>("/auth/login", { email, password }),

  registerAdmin: (email: string, password: string) =>
    apiClient.post<AuthResponse>("/auth/register", { email, password }),

  registerConsultee: (payload: {
    email: string;
    password: string;
    name: string;
    gender: string;
    dob: string;
    address: string;
    phone: string;
  }) => apiClient.post<AuthResponse>("/auth/register-consultee", payload),
};

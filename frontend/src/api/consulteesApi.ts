import { apiClient } from "./client";
import type { Consultee } from "../types";

export type ConsulteeInput = Omit<Consultee, "id">;

export const consulteesApi = {
  list: () => apiClient.get<Consultee[]>("/consultees"),
  get: (id: string) => apiClient.get<Consultee>(`/consultees/id/${id}`),
  create: (payload: ConsulteeInput) => apiClient.post<Consultee>("/consultees", payload),
  update: (id: string, payload: ConsulteeInput) => apiClient.put<Consultee>(`/consultees/id/${id}`, payload),
  remove: (id: string) => apiClient.delete<void>(`/consultees/id/${id}`),
};

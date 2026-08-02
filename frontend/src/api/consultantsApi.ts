import { apiClient } from "./client";
import type { Consultant } from "../types";

export type ConsultantInput = Omit<Consultant, "id">;

export const consultantsApi = {
  list: () => apiClient.get<Consultant[]>("/consultants"),
  get: (id: string) => apiClient.get<Consultant>(`/consultants/id/${id}`),
  create: (payload: ConsultantInput) => apiClient.post<Consultant>("/consultants", payload),
  update: (id: string, payload: ConsultantInput) => apiClient.put<Consultant>(`/consultants/id/${id}`, payload),
  remove: (id: string) => apiClient.delete<void>(`/consultants/id/${id}`),
};

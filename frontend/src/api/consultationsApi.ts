import { apiClient } from "./client";
import type { Consultation } from "../types";

// Matches CreateConsultationRequest exactly: no id, no updatedDate, no server-computed names.
export type ConsultationFormInput = Omit<Consultation, "id" | "updatedDate" | "consultantName" | "consulteeName">;

export const consultationsApi = {
  list: () => apiClient.get<Consultation[]>("/consultations"),
  get: (id: string) => apiClient.get<Consultation>(`/consultations/id/${id}`),
  create: (payload: ConsultationFormInput) => apiClient.post<Consultation>("/consultations", payload),
  update: (id: string, payload: ConsultationFormInput) =>
    apiClient.put<Consultation>(`/consultations/id/${id}`, {
      ...payload,
      updatedDate: new Date().toISOString(),
    }),
  remove: (id: string) => apiClient.delete<void>(`/consultations/id/${id}`),
};

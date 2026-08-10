import { apiClient } from "./client";
import type { Consultation, ConsultationType, Consultee } from "../types";

export interface BookConsultationInput {
  consultantId: string;
  type: ConsultationType;
  consultationDate: string;
  comments?: string;
}

export const portalApi = {
  book: (payload: BookConsultationInput) => apiClient.post<Consultation>("/portal/consultations", payload),
  myConsultations: () => apiClient.get<Consultation[]>("/portal/consultations"),
  myConsultationDetail: (id: string) => apiClient.get<Consultation>(`/portal/consultations/${id}`),
  myProfile: () => apiClient.get<Consultee>("/portal/me"),
  changePassword: (payload: { currentPassword: string; newPassword: string }) =>
    apiClient.post<void>("/portal/change-password", payload),
  changeEmail: (payload: { newEmail: string }) => apiClient.post<Consultee>("/portal/change-email", payload),
};

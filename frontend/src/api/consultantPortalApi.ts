import { apiClient } from "./client";

export interface ConsultantProfile {
  id: string;
  name: string;
  email: string;
  mobile: string | null;
  speciality: string | null;
  qualification: string;
  experienceYears: number;
  fee: number;
}

export const consultantPortalApi = {
  me: () => apiClient.get<ConsultantProfile>("/consultant-portal/me"),
  changePassword: (payload: { currentPassword: string; newPassword: string }) =>
    apiClient.post<void>("/consultant-portal/change-password", payload),
  changeEmail: (payload: { newEmail: string }) =>
    apiClient.post<ConsultantProfile>("/consultant-portal/change-email", payload),
};

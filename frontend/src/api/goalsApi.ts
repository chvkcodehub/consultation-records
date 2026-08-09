import { apiClient } from "./client";
import type { Goal } from "../types";

// Matches CreateGoalRequest/UpdateGoalRequest: no id; createdDate vs updatedDate differ per request.
export type GoalFormInput = Omit<Goal, "id" | "createdDate" | "updatedDate">;

export const goalsApi = {
  list: () => apiClient.get<Goal[]>("/goals"),
  get: (id: string) => apiClient.get<Goal>(`/goals/id/${id}`),
  create: (payload: GoalFormInput) =>
    apiClient.post<Goal>("/goals", { ...payload, createdDate: new Date().toISOString() }),
  update: (id: string, payload: GoalFormInput) =>
    apiClient.put<Goal>(`/goals/id/${id}`, { ...payload, updatedDate: new Date().toISOString() }),
  remove: (id: string) => apiClient.delete<void>(`/goals/id/${id}`),
};

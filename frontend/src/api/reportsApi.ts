import { apiClient } from "./client";
import type { ConsultantSummaryReport, ConsulteeSessionsReport } from "../types";

export const reportsApi = {
  consulteeSessions: () => apiClient.get<ConsulteeSessionsReport>("/reports/consultees/sessions"),
  consultantSummary: () => apiClient.get<ConsultantSummaryReport>("/reports/consultants/summary"),
};

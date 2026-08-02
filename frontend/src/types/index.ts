// Backend serializes java.util.Date as epoch-millis by default; accepts ISO strings on write.
export type ApiDate = string | number | null;

export type Role = "ADMIN" | "CONSULTEE";

export type ConsultationType =
  | "INITIAL_CONSULTATION"
  | "CHILD_DEVELOPMENT"
  | "FOLLOW_UP"
  | "THERAPY_SESSION"
  | "PARENT_CONSULTATION"
  | "EMERGENCY"
  | "ROUTINE_CHECKUP";

export type ConsultationStatus = "BOOKED" | "COMPLETED" | "CANCELLED";

export const CONSULTATION_TYPES: ConsultationType[] = [
  "INITIAL_CONSULTATION",
  "CHILD_DEVELOPMENT",
  "FOLLOW_UP",
  "THERAPY_SESSION",
  "PARENT_CONSULTATION",
  "EMERGENCY",
  "ROUTINE_CHECKUP",
];

export const CONSULTATION_STATUSES: ConsultationStatus[] = ["BOOKED", "COMPLETED", "CANCELLED"];

export interface AuthResponse {
  token: string;
  role: Role;
  consulteeId: string | null;
}

export interface Consultant {
  id: string;
  name: string;
  speciality: string;
  qualification: string;
  experienceYears: number;
  fee: number;
}

export interface Consultee {
  id: string;
  name: string;
  gender: string;
  dob: ApiDate;
  address: string;
  email: string;
  phone: string;
  startDate: ApiDate;
  condition: string | null;
  recoveryStatus: string | null;
}

export interface Consultation {
  id: string;
  type: ConsultationType;
  status: ConsultationStatus;
  consultantId: string;
  consultantName: string | null;
  patientId: string;
  patientName: string | null;
  diagnosis: string | null;
  prescription: string | null;
  comments: string | null;
  consultationDate: ApiDate;
  followUpDate: ApiDate;
  updatedDate: ApiDate;
  createdBy: string | null;
  fee: number | null;
}

export interface Goal {
  id: string;
  name: string;
  description: string;
  importance: string;
  difficulty: string;
  achievingAgeYears: number;
  achievingAgeMonths: number;
  remarks: string;
  periodInMonths: number;
  createdDate: ApiDate;
  updatedDate: ApiDate;
  status: string;
}

export interface ConsulteeSessionBreakdown {
  consulteeId: string;
  consulteeName: string | null;
  sessionCount: number;
}

export interface ConsulteeSessionsReport {
  totalSessions: number;
  breakdown: ConsulteeSessionBreakdown[];
}

export interface ConsultationTypeCount {
  type: ConsultationType;
  count: number;
}

export interface ConsultantSummaryBreakdown {
  consultantId: string;
  consultantName: string | null;
  sessionCount: number;
  byType: ConsultationTypeCount[];
}

export interface ConsultantSummaryReport {
  totalConsultants: number;
  totalSessions: number;
  breakdown: ConsultantSummaryBreakdown[];
}

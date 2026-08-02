import { EntityManager, type FieldConfig } from "../../components/EntityManager";
import { consultationsApi, type ConsultationFormInput } from "../../api/consultationsApi";
import type { Consultation } from "../../types";
import { CONSULTATION_STATUSES, CONSULTATION_TYPES } from "../../types";
import { formatDateTime, labelize, toDateTimeInputValue } from "../../utils/format";

const fields: FieldConfig[] = [
  {
    name: "type",
    label: "Type",
    type: "select",
    required: true,
    options: CONSULTATION_TYPES.map((t) => ({ value: t, label: labelize(t) })),
  },
  {
    name: "status",
    label: "Status",
    type: "select",
    required: true,
    options: CONSULTATION_STATUSES.map((s) => ({ value: s, label: labelize(s) })),
  },
  { name: "consultantId", label: "Consultant ID", type: "text", required: true },
  { name: "patientId", label: "Patient ID", type: "text", required: true },
  { name: "consultationDate", label: "Consultation date/time", type: "datetime-local", required: true },
  { name: "followUpDate", label: "Follow-up date/time", type: "datetime-local" },
  { name: "diagnosis", label: "Diagnosis", type: "textarea" },
  { name: "prescription", label: "Prescription", type: "textarea" },
  { name: "comments", label: "Comments", type: "textarea" },
  { name: "createdBy", label: "Created by", type: "text" },
  { name: "fee", label: "Fee", type: "number" },
];

export function ConsultationsPage() {
  return (
    <EntityManager<Consultation, ConsultationFormInput>
      title="Consultations"
      api={consultationsApi}
      fields={fields}
      columns={[
        { key: "type", label: "Type", render: (item) => labelize(item.type) },
        {
          key: "status",
          label: "Status",
          render: (item) => <span className={`badge status-${item.status}`}>{labelize(item.status)}</span>,
        },
        { key: "consultantId", label: "Consultant", render: (item) => item.consultantName ?? item.consultantId },
        { key: "patientId", label: "Patient", render: (item) => item.patientName ?? item.patientId },
        { key: "consultationDate", label: "When", render: (item) => formatDateTime(item.consultationDate) },
      ]}
      toFormValues={(item) => ({
        type: item?.type ?? "",
        status: item?.status ?? "BOOKED",
        consultantId: item?.consultantId ?? "",
        patientId: item?.patientId ?? "",
        consultationDate: toDateTimeInputValue(item?.consultationDate ?? null),
        followUpDate: toDateTimeInputValue(item?.followUpDate ?? null),
        diagnosis: item?.diagnosis ?? "",
        prescription: item?.prescription ?? "",
        comments: item?.comments ?? "",
        createdBy: item?.createdBy ?? "",
        fee: item?.fee ? String(item.fee) : "",
      })}
      fromFormValues={(values): ConsultationFormInput => ({
        type: values.type as Consultation["type"],
        status: values.status as Consultation["status"],
        consultantId: values.consultantId,
        patientId: values.patientId,
        consultationDate: values.consultationDate ? new Date(values.consultationDate).toISOString() : null,
        followUpDate: values.followUpDate ? new Date(values.followUpDate).toISOString() : null,
        diagnosis: values.diagnosis || null,
        prescription: values.prescription || null,
        comments: values.comments || null,
        createdBy: values.createdBy || null,
        fee: values.fee ? Number(values.fee) : null,
      })}
    />
  );
}

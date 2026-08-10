import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { EntityManager, type FieldConfig } from "../../components/EntityManager";
import { consultationsApi, type ConsultationFormInput } from "../../api/consultationsApi";
import { consultantsApi } from "../../api/consultantsApi";
import { consulteesApi } from "../../api/consulteesApi";
import { useAuth } from "../../auth/useAuth";
import type { Consultation, Consultant, Consultee } from "../../types";
import { CONSULTATION_STATUSES, CONSULTATION_TYPES } from "../../types";
import { formatDateTime, labelize, toDateTimeInputValue } from "../../utils/format";

function ConsultationsPageContent() {
  const { email } = useAuth();
  const [searchParams] = useSearchParams();
  const selectedConsultantId = searchParams.get("consultantId");
  const selectedConsulteeId = searchParams.get("consulteeId");
  const [consultants, setConsultants] = useState<Consultant[]>([]);
  const [consultees, setConsultees] = useState<Consultee[]>([]);

  const handleFieldChange = (name: string, value: string, values: Record<string, string>) => {
    if (name === "consultantId") {
      const selectedConsultant = consultants.find((consultant) => consultant.id === value);
      return {
        ...values,
        fee: selectedConsultant?.fee != null ? String(selectedConsultant.fee) : "",
      };
    }
    return values;
  };

  useEffect(() => {
    Promise.all([consultantsApi.list(), consulteesApi.list()])
      .then(([consultantsResponse, consulteesResponse]) => {
        setConsultants(consultantsResponse);
        setConsultees(consulteesResponse);
      })
      .catch(() => {
        setConsultants([]);
        setConsultees([]);
      });
  }, []);

  const consultationApi = {
    list: async () => {
      const allConsultations = await consultationsApi.list();
      return allConsultations.filter((item) => {
        const matchesConsultant = !selectedConsultantId || item.consultantId === selectedConsultantId;
        const matchesConsultee = !selectedConsulteeId || item.consulteeId === selectedConsulteeId;
        return matchesConsultant && matchesConsultee;
      });
    },
    create: (payload: ConsultationFormInput) => consultationsApi.create(payload),
    update: (id: string, payload: ConsultationFormInput) => consultationsApi.update(id, payload),
    remove: (id: string) => consultationsApi.remove(id),
  };

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
      hiddenOnCreate: true,
    },
    {
      name: "consultantId",
      label: "Consultant",
      type: "select",
      required: true,
      options: consultants.map((consultant) => ({ value: consultant.id, label: consultant.name })),
    },
    {
      name: "consulteeId",
      label: "Consultee",
      type: "select",
      required: true,
      options: consultees.map((consultee) => ({ value: consultee.id, label: consultee.name })),
    },
    { name: "consultationDate", label: "Consultation date/time", type: "datetime-local", required: true },
    { name: "followUpDate", label: "Follow-up date/time", type: "datetime-local", hiddenOnCreate: true },
    { name: "diagnosis", label: "Diagnosis", type: "textarea" },
    { name: "prescription", label: "Prescription", type: "textarea", hiddenOnCreate: true },
    { name: "comments", label: "Comments", type: "textarea" },
    { name: "rating", label: "Rating (1-5)", type: "number" },
    { name: "feedback", label: "Feedback", type: "textarea" },
    { name: "createdBy", label: "Created by", type: "text", hiddenOnCreate: true },
    { name: "fee", label: "Fee", type: "number" },
  ];

  return (
    <EntityManager<Consultation, ConsultationFormInput>
      title="Consultations"
      api={consultationApi}
      fields={fields}
      onFieldChange={handleFieldChange}
      columns={[
        { key: "type", label: "Type", render: (item) => labelize(item.type) },
        {
          key: "status",
          label: "Status",
          render: (item) => <span className={`badge status-${item.status}`}>{labelize(item.status)}</span>,
        },
        { key: "consultantId", label: "Consultant", render: (item) => item.consultantName ?? item.consultantId },
        { key: "consulteeId", label: "Consultee", render: (item) => item.consulteeName ?? item.consulteeId },
        { key: "consultationDate", label: "When", render: (item) => formatDateTime(item.consultationDate) },
      ]}
      toFormValues={(item) => ({
        type: item?.type ?? "",
        status: item?.status ?? "BOOKED",
        consultantId: item?.consultantId ?? selectedConsultantId ?? "",
        consulteeId: item?.consulteeId ?? selectedConsulteeId ?? "",
        consultationDate: toDateTimeInputValue(item?.consultationDate ?? null),
        followUpDate: toDateTimeInputValue(item?.followUpDate ?? null),
        diagnosis: item?.diagnosis ?? "",
        prescription: item?.prescription ?? "",
        comments: item?.comments ?? "",
        rating: item?.rating ? String(item.rating) : "",
        feedback: item?.feedback ?? "",
        createdBy: item?.createdBy ?? email ?? "",
        fee: item?.fee ? String(item.fee) : "",
      })}
      fromFormValues={(values): ConsultationFormInput => ({
        type: values.type as Consultation["type"],
        status: values.status as Consultation["status"],
        consultantId: values.consultantId,
        consulteeId: values.consulteeId,
        consultationDate: values.consultationDate ? new Date(values.consultationDate).toISOString() : null,
        followUpDate: values.followUpDate ? new Date(values.followUpDate).toISOString() : null,
        diagnosis: values.diagnosis || null,
        prescription: values.prescription || null,
        comments: values.comments || null,
        rating: values.rating ? Number(values.rating) : null,
        feedback: values.feedback || null,
        createdBy: values.createdBy || email || null,
        fee: values.fee ? Number(values.fee) : null,
      })}
    />
  );
}

export function ConsultationsPage() {
  return <ConsultationsPageContent />;
}

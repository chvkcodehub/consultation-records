import { EntityManager, type FieldConfig } from "../../components/EntityManager";
import { consulteesApi, type ConsulteeInput } from "../../api/consulteesApi";
import type { Consultee } from "../../types";
import { formatDate, toDateInputValue } from "../../utils/format";

const fields: FieldConfig[] = [
  { name: "name", label: "Name", type: "text", required: true },
  { name: "gender", label: "Gender", type: "text" },
  { name: "dob", label: "Date of birth", type: "date" },
  { name: "address", label: "Address", type: "text" },
  { name: "email", label: "Email", type: "text", required: true },
  { name: "phone", label: "Phone", type: "text" },
  { name: "startDate", label: "Start date", type: "date" },
  { name: "condition", label: "Condition", type: "text" },
  { name: "recoveryStatus", label: "Recovery status", type: "text" },
];

export function ConsulteesPage() {
  return (
    <EntityManager<Consultee, ConsulteeInput>
      title="Consultees"
      api={consulteesApi}
      fields={fields}
      columns={[
        { key: "name", label: "Name" },
        { key: "email", label: "Email" },
        { key: "condition", label: "Condition" },
        { key: "startDate", label: "Start date", render: (item) => formatDate(item.startDate) },
      ]}
      toFormValues={(item) => ({
        name: item?.name ?? "",
        gender: item?.gender ?? "",
        dob: toDateInputValue(item?.dob ?? null),
        address: item?.address ?? "",
        email: item?.email ?? "",
        phone: item?.phone ?? "",
        startDate: toDateInputValue(item?.startDate ?? null),
        condition: item?.condition ?? "",
        recoveryStatus: item?.recoveryStatus ?? "",
      })}
      fromFormValues={(values): ConsulteeInput => ({
        name: values.name,
        gender: values.gender,
        dob: values.dob || null,
        address: values.address,
        email: values.email,
        phone: values.phone,
        startDate: values.startDate || null,
        condition: values.condition || null,
        recoveryStatus: values.recoveryStatus || null,
      })}
    />
  );
}

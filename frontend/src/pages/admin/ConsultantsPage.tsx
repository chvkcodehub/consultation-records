import { EntityManager, type FieldConfig } from "../../components/EntityManager";
import { consultantsApi, type ConsultantInput } from "../../api/consultantsApi";
import type { Consultant } from "../../types";

const fields: FieldConfig[] = [
  { name: "name", label: "Name", type: "text", required: true },
  { name: "speciality", label: "Speciality", type: "text", required: true },
  { name: "qualification", label: "Qualification", type: "text" },
  { name: "experienceYears", label: "Experience (years)", type: "number", required: true },
  { name: "fee", label: "Fee", type: "number", required: true },
];

export function ConsultantsPage() {
  return (
    <EntityManager<Consultant, ConsultantInput>
      title="Consultants"
      api={consultantsApi}
      fields={fields}
      columns={[
        { key: "name", label: "Name" },
        { key: "speciality", label: "Speciality" },
        { key: "experienceYears", label: "Experience" },
        { key: "fee", label: "Fee" },
      ]}
      toFormValues={(item) => ({
        name: item?.name ?? "",
        speciality: item?.speciality ?? "",
        qualification: item?.qualification ?? "",
        experienceYears: item ? String(item.experienceYears) : "0",
        fee: item ? String(item.fee) : "0",
      })}
      fromFormValues={(values): ConsultantInput => ({
        name: values.name,
        speciality: values.speciality,
        qualification: values.qualification,
        experienceYears: Number(values.experienceYears),
        fee: Number(values.fee),
      })}
    />
  );
}

import { EntityManager, type FieldConfig } from "../../components/EntityManager";
import { consultantsApi, type ConsultantInput } from "../../api/consultantsApi";
import type { Consultant, ConsultantSpeciality } from "../../types";

const specialityOptions: Array<{ value: ConsultantSpeciality; label: string }> = [
  { value: "PEDIATRICIAN", label: "Pediatrician" },
  { value: "DEVELOPMENTAL_PEDIATRICIAN", label: "Developmental Pediatrician" },
  { value: "CHILD_PSYCHOLOGIST", label: "Child Psychologist" },
  { value: "CLINICAL_PSYCHOLOGIST", label: "Clinical Psychologist" },
  { value: "OCCUPATIONAL_THERAPIST", label: "Occupational Therapist" },
  { value: "SPEECH_LANGUAGE_PATHOLOGIST", label: "Speech Language Pathologist" },
  { value: "PHYSIOTHERAPIST", label: "Physiotherapist" },
  { value: "SPECIAL_EDUCATOR", label: "Special Educator" },
  { value: "BEHAVIOR_THERAPIST", label: "Behavior Therapist" },
  { value: "SOCIAL_WORKER", label: "Social Worker" },
  { value: "AUDIOLOGIST", label: "Audiologist" },
  { value: "NUTRITIONIST", label: "Nutritionist" },
  { value: "NEUROLOGIST", label: "Neurologist" },
  { value: "PSYCHIATRIST", label: "Psychiatrist" },
];

const qualificationOptions = [
  { value: "Diploma", label: "Diploma" },
  { value: "Graduation", label: "Graduation" },
  { value: "Post Graduation", label: "Post Graduation" },
];

const fields: FieldConfig[] = [
  { name: "name", label: "Name", type: "text", required: true },
  {
    name: "speciality",
    label: "Speciality",
    type: "select",
    required: true,
    options: specialityOptions.map((option) => ({ value: option.value, label: option.label })),
  },
  {
    name: "qualification",
    label: "Qualification",
    type: "select",
    options: qualificationOptions.map((option) => ({ value: option.value, label: option.label })),
  },
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
        speciality: item?.speciality ?? specialityOptions[0].value,
        qualification: item?.qualification ?? "",
        experienceYears: item ? String(item.experienceYears) : "0",
        fee: item ? String(item.fee) : "0",
      })}
      fromFormValues={(values): ConsultantInput => ({
        name: values.name,
        speciality: values.speciality as ConsultantSpeciality,
        qualification: values.qualification,
        experienceYears: Number(values.experienceYears),
        fee: Number(values.fee),
      })}
    />
  );
}

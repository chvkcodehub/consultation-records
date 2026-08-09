import { EntityManager, type FieldConfig } from "../../components/EntityManager";
import { goalsApi, type GoalFormInput } from "../../api/goalsApi";
import type { Goal } from "../../types";

const fields: FieldConfig[] = [
  { name: "name", label: "Name", type: "text", required: true },
  { name: "description", label: "Description", type: "textarea" },
  { name: "importance", label: "Importance", type: "text" },
  { name: "difficulty", label: "Difficulty", type: "text" },
  { name: "achievingAgeYears", label: "Achieving age (years)", type: "number" },
  { name: "achievingAgeMonths", label: "Achieving age (months)", type: "number" },
  { name: "periodInMonths", label: "Period (months)", type: "number" },
  { name: "remarks", label: "Remarks", type: "textarea" },
  { name: "status", label: "Status", type: "text" },
];

export function GoalsPage() {
  return (
    <EntityManager<Goal, GoalFormInput>
      title="Goals"
      api={goalsApi}
      fields={fields}
      columns={[
        { key: "name", label: "Name" },
        { key: "importance", label: "Importance" },
        { key: "difficulty", label: "Difficulty" },
        { key: "status", label: "Status" },
      ]}
      toFormValues={(item) => ({
        name: item?.name ?? "",
        description: item?.description ?? "",
        importance: item?.importance ?? "",
        difficulty: item?.difficulty ?? "",
        achievingAgeYears: item ? String(item.achievingAgeYears) : "0",
        achievingAgeMonths: item ? String(item.achievingAgeMonths) : "0",
        periodInMonths: item ? String(item.periodInMonths) : "0",
        remarks: item?.remarks ?? "",
        status: item?.status ?? "",
      })}
      fromFormValues={(values): GoalFormInput => ({
        name: values.name,
        description: values.description,
        importance: values.importance,
        difficulty: values.difficulty,
        achievingAgeYears: Number(values.achievingAgeYears),
        achievingAgeMonths: Number(values.achievingAgeMonths),
        periodInMonths: Number(values.periodInMonths),
        remarks: values.remarks,
        status: values.status,
      })}
    />
  );
}

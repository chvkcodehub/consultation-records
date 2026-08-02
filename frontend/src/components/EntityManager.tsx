import { useEffect, useState, type ReactNode } from "react";
import { ApiError } from "../api/client";
import { Icon } from "./Icon";

export interface FieldOption {
  value: string;
  label: string;
}

export interface FieldConfig {
  name: string;
  label: string;
  type: "text" | "number" | "date" | "datetime-local" | "select" | "textarea";
  options?: FieldOption[];
  required?: boolean;
  disabledOnEdit?: boolean;
}

export interface ColumnConfig<T> {
  key: string;
  label: string;
  render?: (item: T) => ReactNode;
}

interface EntityManagerProps<T extends { id: string }, TInput> {
  title: string;
  columns: ColumnConfig<T>[];
  fields: FieldConfig[];
  api: {
    list: () => Promise<T[]>;
    create: (payload: TInput) => Promise<T>;
    update: (id: string, payload: TInput) => Promise<T>;
    remove: (id: string) => Promise<void>;
  };
  toFormValues: (item: T | null) => Record<string, string>;
  fromFormValues: (values: Record<string, string>) => TInput;
}

export function EntityManager<T extends { id: string }, TInput>({
  title,
  columns,
  fields,
  api,
  toFormValues,
  fromFormValues,
}: EntityManagerProps<T, TInput>) {
  const [items, setItems] = useState<T[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editing, setEditing] = useState<T | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [values, setValues] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      setItems(await api.list());
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Failed to load data");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const openCreate = () => {
    setEditing(null);
    setValues(toFormValues(null));
    setShowForm(true);
  };

  const openEdit = (item: T) => {
    setEditing(item);
    setValues(toFormValues(item));
    setShowForm(true);
  };

  const closeForm = () => {
    setShowForm(false);
    setEditing(null);
  };

  const handleChange = (name: string, value: string) => {
    setValues((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      const payload = fromFormValues(values);
      if (editing) {
        await api.update(editing.id, payload);
      } else {
        await api.create(payload);
      }
      closeForm();
      await load();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Failed to save");
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (item: T) => {
    if (!confirm(`Delete this record?`)) return;
    setError(null);
    try {
      await api.remove(item.id);
      await load();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Failed to delete");
    }
  };

  return (
    <div>
      <div className="page-header">
        <h2>{title}</h2>
        {!showForm && (
          <button className="primary" onClick={openCreate}>
            <Icon name="plus" size={16} />
            Add new
          </button>
        )}
      </div>

      {error && <p className="error-text">{error}</p>}

      {showForm && (
        <form className="card" onSubmit={handleSubmit}>
          <div className="form-grid">
            {fields.map((field) => {
              const isDisabled = Boolean(editing && field.disabledOnEdit);
              return (
                <label key={field.name} className="form-field">
                  {field.label}
                  {field.type === "select" ? (
                    <select
                      value={values[field.name] ?? ""}
                      required={field.required}
                      disabled={isDisabled}
                      onChange={(e) => handleChange(field.name, e.target.value)}
                    >
                      <option value="" disabled>
                        Select...
                      </option>
                      {field.options?.map((opt) => (
                        <option key={opt.value} value={opt.value}>
                          {opt.label}
                        </option>
                      ))}
                    </select>
                  ) : field.type === "textarea" ? (
                    <textarea
                      value={values[field.name] ?? ""}
                      required={field.required}
                      disabled={isDisabled}
                      onChange={(e) => handleChange(field.name, e.target.value)}
                    />
                  ) : (
                    <input
                      type={field.type}
                      value={values[field.name] ?? ""}
                      required={field.required}
                      disabled={isDisabled}
                      onChange={(e) => handleChange(field.name, e.target.value)}
                    />
                  )}
                </label>
              );
            })}
          </div>
          <div className="form-actions">
            <button className="primary" type="submit" disabled={saving}>
              {editing ? "Save changes" : "Create"}
            </button>
            <button type="button" onClick={closeForm}>
              Cancel
            </button>
          </div>
        </form>
      )}

      {loading ? (
        <div className="loading-state">
          <span className="spinner" />
          Loading...
        </div>
      ) : items.length === 0 ? (
        <div className="table-scroll">
          <div className="empty-state">
            <span className="tile-icon">
              <Icon name="inbox" size={20} />
            </span>
            <span>No records yet.</span>
          </div>
        </div>
      ) : (
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                {columns.map((col) => (
                  <th key={col.key}>{col.label}</th>
                ))}
                <th />
              </tr>
            </thead>
            <tbody>
              {items.map((item) => (
                <tr key={item.id}>
                  {columns.map((col) => (
                    <td key={col.key}>
                      {col.render ? col.render(item) : String((item as Record<string, unknown>)[col.key] ?? "-")}
                    </td>
                  ))}
                  <td>
                    <div className="row-actions">
                      <button className="ghost icon-btn" onClick={() => openEdit(item)}>
                        <Icon name="edit" size={15} />
                        Edit
                      </button>
                      <button className="danger icon-btn" onClick={() => handleDelete(item)}>
                        <Icon name="trash" size={15} />
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

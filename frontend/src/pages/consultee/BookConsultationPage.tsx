import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { consultantsApi } from "../../api/consultantsApi";
import { portalApi } from "../../api/portalApi";
import { ApiError } from "../../api/client";
import type { Consultant, ConsultationType } from "../../types";
import { CONSULTATION_TYPES } from "../../types";
import { labelize } from "../../utils/format";

export function BookConsultationPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [consultants, setConsultants] = useState<Consultant[]>([]);
  const [consultantCode, setConsultantCode] = useState(searchParams.get("consultantCode") ?? "");
  const [type, setType] = useState<ConsultationType>("INITIAL_CONSULTATION");
  const [consultationDate, setConsultationDate] = useState("");
  const [comments, setComments] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    consultantsApi.list().then(setConsultants).catch(() => {});
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      const created = await portalApi.book({
        consultantCode,
        type,
        consultationDate: new Date(consultationDate).toISOString(),
        comments: comments || undefined,
      });
      navigate(`/consultee/consultations/${created.id}`);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Failed to book consultation");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h2>Book a Consultation</h2>
      </div>
      <form className="card" onSubmit={handleSubmit} style={{ maxWidth: 480 }}>
        <div className="form-grid">
          <label className="form-field">
            Consultant
            <select value={consultantCode} onChange={(e) => setConsultantCode(e.target.value)} required>
              <option value="" disabled>
                Select a consultant...
              </option>
              {consultants.map((c) => (
                <option key={c.code} value={c.code}>
                  {c.name} ({c.speciality})
                </option>
              ))}
            </select>
          </label>
          <label className="form-field">
            Consultation type
            <select value={type} onChange={(e) => setType(e.target.value as ConsultationType)} required>
              {CONSULTATION_TYPES.map((t) => (
                <option key={t} value={t}>
                  {labelize(t)}
                </option>
              ))}
            </select>
          </label>
          <label className="form-field">
            Date and time
            <input
              type="datetime-local"
              value={consultationDate}
              onChange={(e) => setConsultationDate(e.target.value)}
              required
            />
          </label>
          <label className="form-field">
            Notes (optional)
            <textarea value={comments} onChange={(e) => setComments(e.target.value)} />
          </label>
        </div>
        {error && <p className="error-text">{error}</p>}
        <div className="form-actions">
          <button className="primary" type="submit" disabled={submitting}>
            Book consultation
          </button>
        </div>
      </form>
    </div>
  );
}

import { useEffect, useMemo, useState } from "react";
import { consultationsApi, type ConsultationFormInput } from "../../api/consultationsApi";
import { consulteesApi } from "../../api/consulteesApi";
import { ApiError } from "../../api/client";
import { useAuth } from "../../auth/useAuth";
import { CONSULTATION_TYPES, type Consultation, type ConsultationType, type Consultee } from "../../types";
import { formatDateTime, labelize } from "../../utils/format";
import { Icon } from "../../components/Icon";

interface SessionFormState {
  consulteeId: string;
  consultationDate: string;
  rating: string;
  feedback: string;
  type: ConsultationType;
}

const INITIAL_FORM: SessionFormState = {
  consulteeId: "",
  consultationDate: "",
  rating: "",
  feedback: "",
  type: "FOLLOW_UP",
};

export function ConsultantSessionsPage() {
  const { consultantId, email } = useAuth();
  const [consultees, setConsultees] = useState<Consultee[]>([]);
  const [sessions, setSessions] = useState<Consultation[]>([]);
  const [form, setForm] = useState<SessionFormState>(INITIAL_FORM);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const sortedSessions = useMemo(
    () =>
      sessions
        .slice()
        .sort((a, b) => new Date(b.consultationDate ?? 0).getTime() - new Date(a.consultationDate ?? 0).getTime()),
    [sessions],
  );

  const loadData = async () => {
    if (!consultantId) {
      setError("This consultant account is not linked to a consultant profile yet.");
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const [consulteeRows, consultationRows] = await Promise.all([
        consulteesApi.list(),
        consultationsApi.listByConsultant(consultantId),
      ]);
      setConsultees(consulteeRows);
      setSessions(consultationRows);
      setForm((prev) => ({ ...prev, consulteeId: prev.consulteeId || consulteeRows[0]?.id || "" }));
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Failed to load session data");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadData();
  }, [consultantId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!consultantId) {
      setError("This consultant account is not linked to a consultant profile yet.");
      return;
    }

    const trimmedFeedback = form.feedback.trim();
    const parsedRating = Number(form.rating);
    if (!Number.isFinite(parsedRating) || parsedRating < 1 || parsedRating > 5) {
      setError("Rating must be a number between 1 and 5.");
      return;
    }

    setSubmitting(true);
    setError(null);

    const payload: ConsultationFormInput = {
      type: form.type,
      status: "COMPLETED",
      consultantId,
      consulteeId: form.consulteeId,
      diagnosis: null,
      prescription: null,
      comments: null,
      rating: parsedRating,
      feedback: trimmedFeedback || null,
      consultationDate: new Date(form.consultationDate).toISOString(),
      followUpDate: null,
      createdBy: email,
      fee: null,
    };

    try {
      const created = await consultationsApi.create(payload);
      setSessions((prev) => [created, ...prev]);
      setForm((prev) => ({ ...INITIAL_FORM, consulteeId: prev.consulteeId }));
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Failed to record session");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h2>Session Records</h2>
          <p className="subtitle">Record completed sessions by consultee with date, rating, and feedback.</p>
        </div>
      </div>

      {error && <p className="error-text">{error}</p>}

      <div className="card">
        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <div className="form-field">
              <label>Consultee</label>
              <select
                value={form.consulteeId}
                onChange={(e) => setForm((prev) => ({ ...prev, consulteeId: e.target.value }))}
                required
              >
                <option value="" disabled>
                  Select consultee
                </option>
                {consultees.map((consultee) => (
                  <option key={consultee.id} value={consultee.id}>
                    {consultee.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-field">
              <label>Consultation type</label>
              <select value={form.type} onChange={(e) => setForm((prev) => ({ ...prev, type: e.target.value as ConsultationType }))}>
                {CONSULTATION_TYPES.map((type) => (
                  <option key={type} value={type}>
                    {labelize(type)}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-field">
              <label>Consultation date/time</label>
              <input
                type="datetime-local"
                value={form.consultationDate}
                onChange={(e) => setForm((prev) => ({ ...prev, consultationDate: e.target.value }))}
                required
              />
            </div>

            <div className="form-field">
              <label>Rating (1-5)</label>
              <input
                type="number"
                min={1}
                max={5}
                step={1}
                value={form.rating}
                onChange={(e) => setForm((prev) => ({ ...prev, rating: e.target.value }))}
                required
              />
            </div>

            <div className="form-field" style={{ gridColumn: "1 / -1" }}>
              <label>Feedback</label>
              <textarea
                value={form.feedback}
                onChange={(e) => setForm((prev) => ({ ...prev, feedback: e.target.value }))}
                placeholder="How did the session go?"
              />
            </div>
          </div>

          <div className="form-actions">
            <button className="primary" type="submit" disabled={submitting || loading}>
              <Icon name="plus" size={16} />
              Record session
            </button>
          </div>
        </form>
      </div>

      {loading ? (
        <div className="loading-state">
          <span className="spinner" />
          Loading...
        </div>
      ) : sortedSessions.length === 0 ? (
        <div className="table-scroll">
          <div className="empty-state">
            <span className="tile-icon">
              <Icon name="inbox" size={20} />
            </span>
            <span>No sessions recorded yet.</span>
          </div>
        </div>
      ) : (
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                <th>When</th>
                <th>Consultee</th>
                <th>Type</th>
                <th>Rating</th>
                <th>Feedback</th>
              </tr>
            </thead>
            <tbody>
              {sortedSessions.map((session) => (
                <tr key={session.id}>
                  <td>{formatDateTime(session.consultationDate)}</td>
                  <td>{session.consulteeName ?? session.consulteeId}</td>
                  <td>{labelize(session.type)}</td>
                  <td>{session.rating ?? "-"}</td>
                  <td>{session.feedback ?? "-"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

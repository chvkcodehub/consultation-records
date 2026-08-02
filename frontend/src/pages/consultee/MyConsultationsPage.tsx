import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { portalApi } from "../../api/portalApi";
import { ApiError } from "../../api/client";
import type { Consultation } from "../../types";
import { formatDateTime, labelize } from "../../utils/format";
import { Icon } from "../../components/Icon";

export function MyConsultationsPage() {
  const [consultations, setConsultations] = useState<Consultation[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    portalApi
      .myConsultations()
      .then(setConsultations)
      .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load consultations"))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div>
      <div className="page-header">
        <div>
          <h2>My Consultations</h2>
          <p className="subtitle">Everything you've booked, past and upcoming.</p>
        </div>
        <Link to="/consultee/book">
          <button className="primary">
            <Icon name="plus" size={16} />
            Book a consultation
          </button>
        </Link>
      </div>
      {error && <p className="error-text">{error}</p>}
      {loading ? (
        <div className="loading-state">
          <span className="spinner" />
          Loading...
        </div>
      ) : consultations.length === 0 ? (
        <div className="table-scroll">
          <div className="empty-state">
            <span className="tile-icon">
              <Icon name="calendar" size={20} />
            </span>
            <span>You have no consultations yet.</span>
          </div>
        </div>
      ) : (
        <div className="table-scroll">
          <table>
            <thead>
              <tr>
                <th>When</th>
                <th>Type</th>
                <th>Status</th>
                <th>Consultant</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {consultations
                .slice()
                .sort(
                  (a, b) => new Date(b.consultationDate ?? 0).getTime() - new Date(a.consultationDate ?? 0).getTime(),
                )
                .map((c) => (
                  <tr key={c.id}>
                    <td>{formatDateTime(c.consultationDate)}</td>
                    <td>{labelize(c.type)}</td>
                    <td>
                      <span className={`badge status-${c.status}`}>{labelize(c.status)}</span>
                    </td>
                    <td>{c.consultantName ?? c.consultantId}</td>
                    <td>
                      <Link to={`/consultee/consultations/${c.id}`}>View</Link>
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

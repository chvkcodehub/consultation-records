import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { consultationsApi } from "../../api/consultationsApi";
import { ApiError } from "../../api/client";
import { useAuth } from "../../auth/useAuth";
import type { Consultation } from "../../types";
import { formatDateTime } from "../../utils/format";
import { Icon } from "../../components/Icon";

interface ConsulteeBreakdownRow {
  consulteeId: string;
  consulteeName: string;
  sessionCount: number;
  avgRating: number | null;
  lastSessionAt: number | null;
}

export function ConsultantDashboardPage() {
  const { consultantId } = useAuth();
  const [sessions, setSessions] = useState<Consultation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!consultantId) {
      setError("This consultant account is not linked to a consultant profile yet.");
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);
    consultationsApi
      .listByConsultant(consultantId)
      .then(setSessions)
      .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load dashboard"))
      .finally(() => setLoading(false));
  }, [consultantId]);

  const breakdown = useMemo<ConsulteeBreakdownRow[]>(() => {
    const grouped = new Map<string, Consultation[]>();
    sessions.forEach((session) => {
      const key = session.consulteeId;
      const list = grouped.get(key) ?? [];
      list.push(session);
      grouped.set(key, list);
    });

    return Array.from(grouped.entries())
      .map(([consulteeId, rows]) => {
        const ratings = rows.map((row) => row.rating).filter((value): value is number => typeof value === "number");
        const latest = rows.reduce<number | null>((maxTs, row) => {
          const ts = row.consultationDate ? new Date(row.consultationDate).getTime() : null;
          if (ts == null || Number.isNaN(ts)) return maxTs;
          if (maxTs == null || ts > maxTs) return ts;
          return maxTs;
        }, null);

        return {
          consulteeId,
          consulteeName: rows[0].consulteeName ?? consulteeId,
          sessionCount: rows.length,
          avgRating: ratings.length ? ratings.reduce((sum, value) => sum + value, 0) / ratings.length : null,
          lastSessionAt: latest,
        };
      })
      .sort((a, b) => b.sessionCount - a.sessionCount);
  }, [sessions]);

  const totalRated = sessions.filter((row) => typeof row.rating === "number").length;
  const overallAverageRating =
    totalRated > 0
      ? sessions
          .map((row) => row.rating)
          .filter((value): value is number => typeof value === "number")
          .reduce((sum, value) => sum + value, 0) / totalRated
      : null;

  return (
    <div>
      <div className="page-header">
        <div>
          <h2>Dashboard by Consultee</h2>
          <p className="subtitle">Track session volume and outcomes for each consultee you have seen.</p>
        </div>
      </div>

      {error && <p className="error-text">{error}</p>}

      {loading ? (
        <div className="loading-state">
          <span className="spinner" />
          Loading...
        </div>
      ) : (
        <>
          <div className="stat-row">
            <div className="stat-tile">
              <span className="tile-icon">
                <Icon name="calendar" size={18} />
              </span>
              <div className="label">Total sessions</div>
              <div className="value">{sessions.length}</div>
            </div>
            <div className="stat-tile">
              <span className="tile-icon teal">
                <Icon name="users" size={18} />
              </span>
              <div className="label">Consultees served</div>
              <div className="value">{breakdown.length}</div>
            </div>
            <div className="stat-tile">
              <span className="tile-icon">
                <Icon name="chart-bar" size={18} />
              </span>
              <div className="label">Average rating</div>
              <div className="value">{overallAverageRating == null ? "-" : overallAverageRating.toFixed(1)}</div>
            </div>
          </div>

          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>Consultee</th>
                  <th>Sessions</th>
                  <th>Average rating</th>
                  <th>Last session</th>
                </tr>
              </thead>
              <tbody>
                {breakdown.map((row) => (
                  <tr key={row.consulteeId}>
                    <td>{row.consulteeName}</td>
                    <td>{row.sessionCount}</td>
                    <td>{row.avgRating == null ? "-" : row.avgRating.toFixed(1)}</td>
                    <td>{row.lastSessionAt == null ? "-" : formatDateTime(new Date(row.lastSessionAt).toISOString())}</td>
                  </tr>
                ))}
                {breakdown.length === 0 && (
                  <tr className="empty-row">
                    <td colSpan={4}>No sessions recorded yet.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          <div className="form-actions" style={{ justifyContent: "flex-end" }}>
            <Link to="/consultant/sessions/record">
              <button className="primary">
                <Icon name="plus" size={16} />
                Record a Session
              </button>
            </Link>
          </div>
        </>
      )}
    </div>
  );
}

import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { reportsApi } from "../../api/reportsApi";
import { ApiError } from "../../api/client";
import type { ConsulteeSessionsReport } from "../../types";
import { Icon } from "../../components/Icon";

export function ReportsConsulteeSessionsPage() {
  const [report, setReport] = useState<ConsulteeSessionsReport | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    reportsApi
      .consulteeSessions()
      .then(setReport)
      .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load report"));
  }, []);

  return (
    <div>
      <div className="page-header">
        <div>
          <h2>Consultee Sessions Report</h2>
          <p className="subtitle">Total consultation sessions, broken down by consultee.</p>
        </div>
      </div>
      {error && <p className="error-text">{error}</p>}
      {report && (
        <>
          <div className="stat-row">
            <div className="stat-tile">
              <span className="tile-icon">
                <Icon name="calendar" size={18} />
              </span>
              <div className="label">Total sessions across all consultees</div>
              <div className="value">{report.totalSessions}</div>
            </div>
            <div className="stat-tile">
              <span className="tile-icon teal">
                <Icon name="users" size={18} />
              </span>
              <div className="label">Consultees seen</div>
              <div className="value">{report.breakdown.length}</div>
            </div>
          </div>
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>Consultee ID</th>
                  <th>Consultee name</th>
                  <th>Sessions</th>
                </tr>
              </thead>
              <tbody>
                {report.breakdown
                  .sort((a, b) => b.sessionCount - a.sessionCount)
                  .map((row) => (
                    <tr key={row.consulteeId}>
                      <td>{row.consulteeId}</td>
                      <td>
                        {row.consulteeName ? (
                          <Link to={`/admin/consultations?consulteeId=${row.consulteeId}`}>{row.consulteeName}</Link>
                        ) : (
                          "-"
                        )}
                      </td>
                      <td>{row.sessionCount}</td>
                    </tr>
                  ))}
                {report.breakdown.length === 0 && (
                  <tr>
                    <td colSpan={3}>No sessions recorded yet.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  );
}

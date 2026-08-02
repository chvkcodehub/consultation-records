import { useEffect, useState } from "react";
import { reportsApi } from "../../api/reportsApi";
import { ApiError } from "../../api/client";
import type { ConsulteeSessionsReport } from "../../types";

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
        <h2>Consultee Sessions Report</h2>
      </div>
      {error && <p className="error-text">{error}</p>}
      {report && (
        <>
          <div className="stat-row">
            <div className="stat-tile">
              <div className="label">Total sessions across all patients</div>
              <div className="value">{report.totalSessions}</div>
            </div>
            <div className="stat-tile">
              <div className="label">Consultees seen</div>
              <div className="value">{report.breakdown.length}</div>
            </div>
          </div>
          <table>
            <thead>
              <tr>
                <th>Consultee code</th>
                <th>Consultee name</th>
                <th>Sessions</th>
              </tr>
            </thead>
            <tbody>
              {report.breakdown
                .sort((a, b) => b.sessionCount - a.sessionCount)
                .map((row) => (
                  <tr key={row.consulteeCode}>
                    <td>{row.consulteeCode}</td>
                    <td>{row.consulteeName ?? "-"}</td>
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
        </>
      )}
    </div>
  );
}

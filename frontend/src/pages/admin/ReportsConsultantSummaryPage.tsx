import { useEffect, useState } from "react";
import { reportsApi } from "../../api/reportsApi";
import { ApiError } from "../../api/client";
import type { ConsultantSummaryReport } from "../../types";
import { labelize } from "../../utils/format";

export function ReportsConsultantSummaryPage() {
  const [report, setReport] = useState<ConsultantSummaryReport | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    reportsApi
      .consultantSummary()
      .then(setReport)
      .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load report"));
  }, []);

  return (
    <div>
      <div className="page-header">
        <h2>Consultant Summary Report</h2>
      </div>
      {error && <p className="error-text">{error}</p>}
      {report && (
        <>
          <div className="stat-row">
            <div className="stat-tile">
              <div className="label">Total consultants</div>
              <div className="value">{report.totalConsultants}</div>
            </div>
            <div className="stat-tile">
              <div className="label">Total sessions</div>
              <div className="value">{report.totalSessions}</div>
            </div>
          </div>
          <table>
            <thead>
              <tr>
                <th>Consultant code</th>
                <th>Consultant name</th>
                <th>Sessions</th>
                <th>Breakdown by type</th>
              </tr>
            </thead>
            <tbody>
              {report.breakdown
                .sort((a, b) => b.sessionCount - a.sessionCount)
                .map((row) => (
                  <tr key={row.consultantCode}>
                    <td>{row.consultantCode}</td>
                    <td>{row.consultantName ?? "-"}</td>
                    <td>{row.sessionCount}</td>
                    <td>
                      {row.byType.map((tc) => (
                        <span key={tc.type} className="badge" style={{ marginRight: "0.35rem" }}>
                          {labelize(tc.type)}: {tc.count}
                        </span>
                      ))}
                    </td>
                  </tr>
                ))}
              {report.breakdown.length === 0 && (
                <tr>
                  <td colSpan={4}>No sessions recorded yet.</td>
                </tr>
              )}
            </tbody>
          </table>
        </>
      )}
    </div>
  );
}

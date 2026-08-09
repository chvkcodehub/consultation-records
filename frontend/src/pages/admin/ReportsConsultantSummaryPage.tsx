import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { reportsApi } from "../../api/reportsApi";
import { ApiError } from "../../api/client";
import type { ConsultantSummaryReport } from "../../types";
import { labelize } from "../../utils/format";
import { Icon } from "../../components/Icon";

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
        <div>
          <h2>Consultant Summary Report</h2>
          <p className="subtitle">Sessions per consultant, broken down by consultation type.</p>
        </div>
      </div>
      {error && <p className="error-text">{error}</p>}
      {report && (
        <>
          <div className="stat-row">
            <div className="stat-tile">
              <span className="tile-icon">
                <Icon name="stethoscope" size={18} />
              </span>
              <div className="label">Total consultants</div>
              <div className="value">{report.totalConsultants}</div>
            </div>
            <div className="stat-tile">
              <span className="tile-icon teal">
                <Icon name="chart-bar" size={18} />
              </span>
              <div className="label">Total sessions</div>
              <div className="value">{report.totalSessions}</div>
            </div>
          </div>
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>Consultant ID</th>
                  <th>Consultant name</th>
                  <th>Sessions</th>
                  <th>Breakdown by type</th>
                </tr>
              </thead>
              <tbody>
                {report.breakdown
                  .sort((a, b) => b.sessionCount - a.sessionCount)
                  .map((row) => (
                    <tr key={row.consultantId}>
                      <td>{row.consultantId}</td>
                      <td>
                        {row.consultantName ? (
                          <Link to={`/admin/consultations?consultantId=${row.consultantId}`}>{row.consultantName}</Link>
                        ) : (
                          "-"
                        )}
                      </td>
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
          </div>
        </>
      )}
    </div>
  );
}

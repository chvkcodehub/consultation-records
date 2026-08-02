import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { reportsApi } from "../../api/reportsApi";
import type { ConsultantSummaryReport, ConsulteeSessionsReport } from "../../types";

export function DashboardPage() {
  const [consulteeReport, setConsulteeReport] = useState<ConsulteeSessionsReport | null>(null);
  const [consultantReport, setConsultantReport] = useState<ConsultantSummaryReport | null>(null);

  useEffect(() => {
    reportsApi.consulteeSessions().then(setConsulteeReport).catch(() => {});
    reportsApi.consultantSummary().then(setConsultantReport).catch(() => {});
  }, []);

  return (
    <div>
      <div className="page-header">
        <h2>Dashboard</h2>
      </div>
      <div className="stat-row">
        <div className="stat-tile">
          <div className="label">Total sessions</div>
          <div className="value">{consulteeReport?.totalSessions ?? "-"}</div>
        </div>
        <div className="stat-tile">
          <div className="label">Consultees with sessions</div>
          <div className="value">{consulteeReport?.breakdown.length ?? "-"}</div>
        </div>
        <div className="stat-tile">
          <div className="label">Active consultants</div>
          <div className="value">{consultantReport?.totalConsultants ?? "-"}</div>
        </div>
      </div>
      <div className="card">
        <p>
          Manage <Link to="/admin/consultants">consultants</Link>, <Link to="/admin/consultees">consultees</Link>,{" "}
          <Link to="/admin/consultations">consultations</Link> and <Link to="/admin/goals">goals</Link>.
        </p>
        <p>
          View the <Link to="/admin/reports/consultees">consultee sessions report</Link> or the{" "}
          <Link to="/admin/reports/consultants">consultant summary report</Link>.
        </p>
      </div>
    </div>
  );
}

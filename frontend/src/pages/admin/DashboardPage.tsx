import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { reportsApi } from "../../api/reportsApi";
import type { ConsultantSummaryReport, ConsulteeSessionsReport } from "../../types";
import { Icon } from "../../components/Icon";

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
        <div>
          <h2>Dashboard</h2>
          <p className="subtitle">A quick look at how the practice is running.</p>
        </div>
      </div>
      <div className="stat-row">
        <Link to="/admin/consultations" className="stat-tile" style={{ textDecoration: "none", color: "inherit" }}>
          <span className="tile-icon">
            <Icon name="calendar" size={18} />
          </span>
          <div className="label">Total sessions</div>
          <div className="value">{consulteeReport?.totalSessions ?? "-"}</div>
        </Link>
        <Link to="/admin/consultees" className="stat-tile" style={{ textDecoration: "none", color: "inherit" }}>
          <span className="tile-icon teal">
            <Icon name="users" size={18} />
          </span>
          <div className="label">Consultees with sessions</div>
          <div className="value">{consulteeReport?.breakdown.length ?? "-"}</div>
        </Link>
        <Link to="/admin/consultants" className="stat-tile" style={{ textDecoration: "none", color: "inherit" }}>
          <span className="tile-icon">
            <Icon name="stethoscope" size={18} />
          </span>
          <div className="label">Active consultants</div>
          <div className="value">{consultantReport?.totalConsultants ?? "-"}</div>
        </Link>
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

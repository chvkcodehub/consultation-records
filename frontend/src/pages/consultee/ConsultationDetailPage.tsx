import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { portalApi } from "../../api/portalApi";
import { ApiError } from "../../api/client";
import type { Consultation } from "../../types";
import { formatDateTime, labelize } from "../../utils/format";
import { Icon, type IconName } from "../../components/Icon";

function DetailRow({ icon, label, value }: { icon: IconName; label: string; value: React.ReactNode }) {
  return (
    <div className="detail-row">
      <span className="tile-icon">
        <Icon name={icon} size={16} />
      </span>
      <div>
        <div className="detail-label">{label}</div>
        <div className="detail-value">{value}</div>
      </div>
    </div>
  );
}

export function ConsultationDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [consultation, setConsultation] = useState<Consultation | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    portalApi
      .myConsultationDetail(id)
      .then(setConsultation)
      .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load consultation"));
  }, [id]);

  return (
    <div>
      <div className="page-header">
        <h2>Consultation Detail</h2>
        <Link to="/consultee">
          <button className="ghost icon-btn">
            <Icon name="arrow-right" size={15} style={{ transform: "rotate(180deg)" }} />
            Back to my consultations
          </button>
        </Link>
      </div>
      {error && <p className="error-text">{error}</p>}
      {consultation && (
        <div className="card detail-grid">
          <DetailRow icon="target" label="Type" value={labelize(consultation.type)} />
          <DetailRow
            icon="clock"
            label="Status"
            value={<span className={`badge status-${consultation.status}`}>{labelize(consultation.status)}</span>}
          />
          <DetailRow
            icon="stethoscope"
            label="Consultant"
            value={consultation.consultantName ?? consultation.consultantId}
          />
          <DetailRow icon="calendar" label="When" value={formatDateTime(consultation.consultationDate)} />
          {consultation.followUpDate && (
            <DetailRow icon="calendar" label="Follow-up" value={formatDateTime(consultation.followUpDate)} />
          )}
          {consultation.diagnosis && <DetailRow icon="inbox" label="Diagnosis" value={consultation.diagnosis} />}
          {consultation.prescription && (
            <DetailRow icon="inbox" label="Prescription" value={consultation.prescription} />
          )}
          {consultation.comments && <DetailRow icon="inbox" label="Notes" value={consultation.comments} />}
          {consultation.fee != null && <DetailRow icon="chart-bar" label="Fee" value={consultation.fee} />}
        </div>
      )}
    </div>
  );
}

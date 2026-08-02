import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { portalApi } from "../../api/portalApi";
import { ApiError } from "../../api/client";
import type { Consultation } from "../../types";
import { formatDateTime, labelize } from "../../utils/format";

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
        <Link to="/consultee">Back to my consultations</Link>
      </div>
      {error && <p className="error-text">{error}</p>}
      {consultation && (
        <div className="card">
          <p>
            <strong>Code:</strong> {consultation.code}
          </p>
          <p>
            <strong>Type:</strong> {labelize(consultation.type)}
          </p>
          <p>
            <strong>Status:</strong> <span className={`badge status-${consultation.status}`}>{labelize(consultation.status)}</span>
          </p>
          <p>
            <strong>Consultant:</strong> {consultation.consultantCode}
          </p>
          <p>
            <strong>When:</strong> {formatDateTime(consultation.consultationDate)}
          </p>
          {consultation.followUpDate && (
            <p>
              <strong>Follow-up:</strong> {formatDateTime(consultation.followUpDate)}
            </p>
          )}
          {consultation.diagnosis && (
            <p>
              <strong>Diagnosis:</strong> {consultation.diagnosis}
            </p>
          )}
          {consultation.prescription && (
            <p>
              <strong>Prescription:</strong> {consultation.prescription}
            </p>
          )}
          {consultation.comments && (
            <p>
              <strong>Notes:</strong> {consultation.comments}
            </p>
          )}
          {consultation.fee != null && (
            <p>
              <strong>Fee:</strong> {consultation.fee}
            </p>
          )}
        </div>
      )}
    </div>
  );
}

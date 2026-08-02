import { useEffect, useState } from "react";
import { portalApi } from "../../api/portalApi";
import { ApiError } from "../../api/client";
import type { Consultee } from "../../types";
import { formatDate } from "../../utils/format";

export function MyProfilePage() {
  const [profile, setProfile] = useState<Consultee | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    portalApi
      .myProfile()
      .then(setProfile)
      .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load profile"));
  }, []);

  return (
    <div>
      <div className="page-header">
        <h2>My Profile</h2>
      </div>
      {error && <p className="error-text">{error}</p>}
      {profile && (
        <div className="card">
          <p>
            <strong>Code:</strong> {profile.code}
          </p>
          <p>
            <strong>Name:</strong> {profile.name}
          </p>
          <p>
            <strong>Gender:</strong> {profile.gender || "-"}
          </p>
          <p>
            <strong>Date of birth:</strong> {formatDate(profile.dob)}
          </p>
          <p>
            <strong>Email:</strong> {profile.email}
          </p>
          <p>
            <strong>Phone:</strong> {profile.phone || "-"}
          </p>
          <p>
            <strong>Address:</strong> {profile.address || "-"}
          </p>
          <p>
            <strong>Care started:</strong> {formatDate(profile.startDate)}
          </p>
          <p>
            <strong>Condition:</strong> {profile.condition || "-"}
          </p>
          <p>
            <strong>Recovery status:</strong> {profile.recoveryStatus || "-"}
          </p>
        </div>
      )}
    </div>
  );
}

import { useEffect, useState } from "react";
import { portalApi } from "../../api/portalApi";
import { ApiError } from "../../api/client";
import type { Consultee } from "../../types";
import { formatDate } from "../../utils/format";
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
        <div>
          <h2>My Profile</h2>
          <p className="subtitle">Your details on file with the practice.</p>
        </div>
      </div>
      {error && <p className="error-text">{error}</p>}
      {profile && (
        <div className="card detail-grid">
          <DetailRow icon="user-circle" label="Name" value={profile.name} />
          <DetailRow icon="users" label="Gender" value={profile.gender || "-"} />
          <DetailRow icon="calendar" label="Date of birth" value={formatDate(profile.dob)} />
          <DetailRow icon="mail" label="Email" value={profile.email} />
          <DetailRow icon="inbox" label="Phone" value={profile.phone || "-"} />
          <DetailRow icon="inbox" label="Address" value={profile.address || "-"} />
          <DetailRow icon="clock" label="Care started" value={formatDate(profile.startDate)} />
          <DetailRow icon="target" label="Condition" value={profile.condition || "-"} />
          <DetailRow icon="chart-pie" label="Recovery status" value={profile.recoveryStatus || "-"} />
        </div>
      )}
    </div>
  );
}

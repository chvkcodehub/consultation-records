import { useEffect, useState } from "react";
import { portalApi } from "../../api/portalApi";
import { ApiError } from "../../api/client";
import { useAuth } from "../../auth/useAuth";
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
  const { updateEmail } = useAuth();
  const [profile, setProfile] = useState<Consultee | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [editingEmail, setEditingEmail] = useState(false);
  const [newEmail, setNewEmail] = useState("");
  const [savingEmail, setSavingEmail] = useState(false);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmNewPassword, setConfirmNewPassword] = useState("");
  const [savingPassword, setSavingPassword] = useState(false);

  useEffect(() => {
    portalApi
      .myProfile()
      .then((result) => {
        setProfile(result);
        setNewEmail(result.email);
      })
      .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load profile"));
  }, []);

  const onChangeEmail = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setSavingEmail(true);
    try {
      const updated = await portalApi.changeEmail({ newEmail });
      setProfile(updated);
      updateEmail(updated.email);
      setEditingEmail(false);
      setSuccess("Email updated successfully.");
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Failed to update email");
    } finally {
      setSavingEmail(false);
    }
  };

  const onChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (newPassword !== confirmNewPassword) {
      setError("New password and confirm password must match.");
      return;
    }

    setSavingPassword(true);
    try {
      await portalApi.changePassword({ currentPassword, newPassword });
      setCurrentPassword("");
      setNewPassword("");
      setConfirmNewPassword("");
      setShowPasswordModal(false);
      setSuccess("Password changed successfully.");
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Failed to update password");
    } finally {
      setSavingPassword(false);
    }
  };

  return (
    <div className="consultee-profile-page">
      <div className="page-header">
        <div>
          <h2>My Profile</h2>
          <p className="subtitle">Your details on file with the practice.</p>
        </div>
      </div>
      {error && <p className="error-text">{error}</p>}
      {success && <p className="auth-links">{success}</p>}
      {profile && (
        <>
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

          <form className="card email-card" onSubmit={onChangeEmail}>
            <h3>Change Email</h3>
            <p className="subtitle">Keep your account updated with your latest login email.</p>
            <div className="form-grid single-column">
              <label className="form-field">
                Email
                {editingEmail ? (
                  <input type="email" value={newEmail} onChange={(e) => setNewEmail(e.target.value)} required />
                ) : (
                  <input type="email" value={newEmail} disabled readOnly />
                )}
              </label>
            </div>
            <div className="form-actions">
              {editingEmail ? (
                <>
                  <button className="primary" type="submit" disabled={savingEmail}>
                    Update email
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      setEditingEmail(false);
                      setNewEmail(profile.email);
                    }}
                  >
                    Cancel
                  </button>
                </>
              ) : (
                <button className="primary" type="button" onClick={() => setEditingEmail(true)}>
                  Change Email
                </button>
              )}
            </div>
          </form>

          <div className="card">
            <h3>Password</h3>
            <p className="subtitle">Keep your account secure by updating your password regularly.</p>
            <div className="form-actions">
              <button className="primary" type="button" onClick={() => setShowPasswordModal(true)}>
                Change Password
              </button>
            </div>
          </div>

          {showPasswordModal ? (
            <div className="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="consultee-password-modal-title">
              <div className="modal-card password-modal-card">
                <div className="modal-header">
                  <h3 id="consultee-password-modal-title">Change Password</h3>
                  <button type="button" className="ghost" onClick={() => setShowPasswordModal(false)}>
                    Close
                  </button>
                </div>
                <form onSubmit={onChangePassword}>
                  <div className="form-grid single-column">
                    <label className="form-field">
                      Current password
                      <input
                        type="password"
                        value={currentPassword}
                        onChange={(e) => setCurrentPassword(e.target.value)}
                        required
                      />
                    </label>
                    <label className="form-field">
                      New password
                      <input
                        type="password"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        required
                      />
                    </label>
                    <label className="form-field">
                      Confirm new password
                      <input
                        type="password"
                        value={confirmNewPassword}
                        onChange={(e) => setConfirmNewPassword(e.target.value)}
                        required
                      />
                    </label>
                  </div>
                  <div className="form-actions">
                    <button className="primary" type="submit" disabled={savingPassword}>
                      Update password
                    </button>
                    <button type="button" onClick={() => setShowPasswordModal(false)}>
                      Cancel
                    </button>
                  </div>
                </form>
              </div>
            </div>
          ) : null}
        </>
      )}
    </div>
  );
}

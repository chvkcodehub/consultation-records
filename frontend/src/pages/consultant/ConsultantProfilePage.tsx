import { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { consultantPortalApi, type ConsultantProfile } from "../../api/consultantPortalApi";
import { ApiError } from "../../api/client";
import { useAuth } from "../../auth/useAuth";

export function ConsultantProfilePage() {
  const { updateEmail, clearPasswordChangeRequired, passwordChangeRequired } = useAuth();
  const [searchParams] = useSearchParams();
  const firstLogin = searchParams.get("firstLogin") === "1" || passwordChangeRequired;

  const [profile, setProfile] = useState<ConsultantProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const [newEmail, setNewEmail] = useState("");
  const [editingEmail, setEditingEmail] = useState(false);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmNewPassword, setConfirmNewPassword] = useState("");
  const [savingEmail, setSavingEmail] = useState(false);
  const [savingPassword, setSavingPassword] = useState(false);

  useEffect(() => {
    if (firstLogin) {
      setShowPasswordModal(true);
    }
  }, [firstLogin]);

  useEffect(() => {
    consultantPortalApi
      .me()
      .then((result) => {
        setProfile(result);
        setNewEmail(result.email);
      })
      .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load profile"))
      .finally(() => setLoading(false));
  }, []);

  const profileRows = useMemo(
    () => [
      { label: "Name", value: profile?.name ?? "-" },
      { label: "Mobile", value: profile?.mobile || "-" },
      { label: "Speciality", value: profile?.speciality || "-" },
      { label: "Qualification", value: profile?.qualification || "-" },
      { label: "Experience", value: profile ? `${profile.experienceYears} years` : "-" },
      { label: "Fee", value: profile ? `${profile.fee}` : "-" },
    ],
    [profile],
  );

  const onChangeEmail = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setSavingEmail(true);
    try {
      const updated = await consultantPortalApi.changeEmail({ newEmail });
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
      await consultantPortalApi.changePassword({
        currentPassword,
        newPassword,
      });
      setCurrentPassword("");
      setNewPassword("");
      setConfirmNewPassword("");
      clearPasswordChangeRequired();
      setShowPasswordModal(false);
      setSuccess("Password changed successfully.");
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Failed to update password");
    } finally {
      setSavingPassword(false);
    }
  };

  return (
    <div className="consultant-profile-page">
      <div className="page-header">
        <div>
          <h2>My Profile</h2>
          <p className="subtitle">
            {firstLogin
              ? "First login detected. Please change your temporary password before continuing."
              : "Manage your login email and password."}
          </p>
        </div>
      </div>

      {error && <p className="error-text">{error}</p>}
      {success && <p className="auth-links">{success}</p>}

      {loading ? (
        <div className="loading-state">
          <span className="spinner" />
          Loading...
        </div>
      ) : (
        <>
          <div className="card detail-grid">
            {profileRows.map((row) => (
              <div key={row.label} className="detail-row">
                <div>
                  <div className="detail-label">{row.label}</div>
                  <div className="detail-value">{row.value}</div>
                </div>
              </div>
            ))}
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
                      setNewEmail(profile?.email ?? "");
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
                {firstLogin ? "Set New Password" : "Change Password"}
              </button>
            </div>
          </div>

          {showPasswordModal ? (
            <div className="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="password-modal-title">
              <div className="modal-card password-modal-card">
                <div className="modal-header">
                  <h3 id="password-modal-title">{firstLogin ? "Set New Password" : "Change Password"}</h3>
                  {!firstLogin ? (
                    <button type="button" className="ghost" onClick={() => setShowPasswordModal(false)}>
                      Close
                    </button>
                  ) : null}
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
                    {!firstLogin ? (
                      <button type="button" onClick={() => setShowPasswordModal(false)}>
                        Cancel
                      </button>
                    ) : null}
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

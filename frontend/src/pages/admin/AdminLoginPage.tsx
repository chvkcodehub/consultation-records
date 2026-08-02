import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authApi } from "../../api/authApi";
import { ApiError } from "../../api/client";
import { useAuth } from "../../auth/useAuth";
import { AuthShell } from "../../components/AuthShell";
import { Icon } from "../../components/Icon";

export function AdminLoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      const auth = await authApi.login(email, password);
      if (auth.role !== "ADMIN") {
        setError("This account is not an administrator account.");
        return;
      }
      login(auth, email);
      navigate("/admin");
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Login failed");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthShell title="Admin sign in" subtitle="Manage consultants, consultees, and reports.">
      <form onSubmit={handleSubmit}>
        <div className="form-field">
          <label>Email</label>
          <div className="field-icon">
            <Icon name="mail" size={16} />
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          </div>
        </div>
        <div className="form-field" style={{ marginTop: "0.75rem" }}>
          <label>Password</label>
          <div className="field-icon">
            <Icon name="lock" size={16} />
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
          </div>
        </div>
        {error && <p className="error-text">{error}</p>}
        <div className="form-actions">
          <button className="primary" type="submit" disabled={submitting} style={{ width: "100%", justifyContent: "center" }}>
            Sign in
          </button>
        </div>
      </form>
      <p className="auth-links">
        Are you a consultee? <Link to="/consultee/login">Sign in here</Link>
      </p>
      <p className="auth-links">
        No admin account yet? <Link to="/admin/register">Create one</Link>
      </p>
    </AuthShell>
  );
}

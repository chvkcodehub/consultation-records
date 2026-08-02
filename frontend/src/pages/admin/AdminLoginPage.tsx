import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authApi } from "../../api/authApi";
import { ApiError } from "../../api/client";
import { useAuth } from "../../auth/useAuth";

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
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>Admin sign in</h1>
        <div className="form-field">
          <label>Email</label>
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </div>
        <div className="form-field" style={{ marginTop: "0.75rem" }}>
          <label>Password</label>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </div>
        {error && <p className="error-text">{error}</p>}
        <div className="form-actions">
          <button className="primary" type="submit" disabled={submitting}>
            Sign in
          </button>
        </div>
        <p className="auth-links">
          Are you a consultee? <Link to="/consultee/login">Sign in here</Link>
        </p>
        <p className="auth-links">
          No admin account yet? <Link to="/admin/register">Create one</Link>
        </p>
      </form>
    </div>
  );
}

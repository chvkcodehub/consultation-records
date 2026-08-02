import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authApi } from "../../api/authApi";
import { ApiError } from "../../api/client";
import { useAuth } from "../../auth/useAuth";

export function ConsulteeRegisterPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    email: "",
    password: "",
    name: "",
    gender: "",
    dob: "",
    address: "",
    phone: "",
  });
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const update = (field: keyof typeof form, value: string) => setForm((prev) => ({ ...prev, [field]: value }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      const auth = await authApi.registerConsultee({
        ...form,
        dob: form.dob ? new Date(form.dob).toISOString() : "",
      });
      login(auth, form.email);
      navigate("/consultee");
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Registration failed");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-page">
      <form className="auth-card" style={{ maxWidth: 460 }} onSubmit={handleSubmit}>
        <h1>Create your account</h1>
        <div className="form-grid">
          <label className="form-field">
            Full name
            <input value={form.name} onChange={(e) => update("name", e.target.value)} required />
          </label>
          <label className="form-field">
            Email
            <input type="email" value={form.email} onChange={(e) => update("email", e.target.value)} required />
          </label>
          <label className="form-field">
            Password
            <input
              type="password"
              value={form.password}
              onChange={(e) => update("password", e.target.value)}
              required
            />
          </label>
          <label className="form-field">
            Gender
            <input value={form.gender} onChange={(e) => update("gender", e.target.value)} />
          </label>
          <label className="form-field">
            Date of birth
            <input type="date" value={form.dob} onChange={(e) => update("dob", e.target.value)} />
          </label>
          <label className="form-field">
            Phone
            <input value={form.phone} onChange={(e) => update("phone", e.target.value)} />
          </label>
          <label className="form-field">
            Address
            <input value={form.address} onChange={(e) => update("address", e.target.value)} />
          </label>
        </div>
        {error && <p className="error-text">{error}</p>}
        <div className="form-actions">
          <button className="primary" type="submit" disabled={submitting}>
            Create account
          </button>
        </div>
        <p className="auth-links">
          Already registered? <Link to="/consultee/login">Sign in</Link>
        </p>
      </form>
    </div>
  );
}

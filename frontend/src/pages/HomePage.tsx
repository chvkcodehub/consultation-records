import { Navigate, Link } from "react-router-dom";
import { useAuth } from "../auth/useAuth";

export function HomePage() {
  const { isAuthenticated, role } = useAuth();

  if (isAuthenticated) {
    return <Navigate to={role === "ADMIN" ? "/admin" : "/consultee"} replace />;
  }

  return (
    <div className="auth-page">
      <div className="auth-card" style={{ textAlign: "center" }}>
        <h1>Consultation Records</h1>
        <p>Choose how you'd like to sign in.</p>
        <div className="form-actions" style={{ justifyContent: "center" }}>
          <Link to="/admin/login">
            <button className="primary">Administrator</button>
          </Link>
          <Link to="/consultee/login">
            <button>Consultee</button>
          </Link>
        </div>
      </div>
    </div>
  );
}

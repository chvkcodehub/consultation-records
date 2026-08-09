import { Navigate, Link } from "react-router-dom";
import { useAuth } from "../auth/useAuth";
import { AuthShell } from "../components/AuthShell";
import { Icon } from "../components/Icon";

export function HomePage() {
  const { isAuthenticated, role } = useAuth();

  if (isAuthenticated) {
    return <Navigate to={role === "ADMIN" ? "/admin" : "/consultee"} replace />;
  }

  return (
    <AuthShell title="Consultation Records" subtitle="Choose how you'd like to sign in.">
      <div className="form-actions" style={{ justifyContent: "center", marginTop: 0 }}>
        <Link to="/admin/login" style={{ flex: 1 }}>
          <button className="primary" style={{ width: "100%", justifyContent: "center" }}>
            <Icon name="dashboard" size={16} />
            Administrator
          </button>
        </Link>
        <Link to="/consultee/login" style={{ flex: 1 }}>
          <button style={{ width: "100%", justifyContent: "center" }}>
            <Icon name="user-circle" size={16} />
            Consultee
          </button>
        </Link>
      </div>
    </AuthShell>
  );
}

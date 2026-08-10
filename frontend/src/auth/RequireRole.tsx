import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "./useAuth";
import type { Role } from "../types";

export function RequireRole({ role }: { role: Role }) {
  const { isAuthenticated, role: currentRole, passwordChangeRequired } = useAuth();
  const location = useLocation();

  const appRouteForRole = (targetRole: Role) => {
    if (targetRole === "ADMIN") return "/admin";
    if (targetRole === "CONSULTANT") return "/consultant";
    return "/consultee";
  };

  if (!isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  if (currentRole !== role) {
    return <Navigate to={appRouteForRole(currentRole ?? "CONSULTEE")} replace />;
  }

  if (role === "CONSULTANT" && passwordChangeRequired && location.pathname !== "/consultant/profile") {
    return <Navigate to="/consultant/profile?firstLogin=1" replace />;
  }

  return <Outlet />;
}

import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "./useAuth";
import type { Role } from "../types";

export function RequireRole({ role }: { role: Role }) {
  const { isAuthenticated, role: currentRole } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to={role === "ADMIN" ? "/admin/login" : "/consultee/login"} replace />;
  }

  if (currentRole !== role) {
    return <Navigate to={currentRole === "ADMIN" ? "/admin" : "/consultee"} replace />;
  }

  return <Outlet />;
}

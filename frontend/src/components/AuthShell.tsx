import type { CSSProperties, ReactNode } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../auth/useAuth";
import { Icon } from "./Icon";

interface AuthShellProps {
  title: string;
  subtitle?: string;
  children: ReactNode;
  maxWidth?: number;
}

export function AuthShell({ title, subtitle, children, maxWidth }: AuthShellProps) {
  const { isAuthenticated, role } = useAuth();
  const style: CSSProperties | undefined = maxWidth ? { maxWidth } : undefined;
  const homeRoute = isAuthenticated
    ? role === "ADMIN"
      ? "/admin"
      : role === "CONSULTANT"
        ? "/consultant"
        : "/consultee"
    : "/";

  return (
    <div className="auth-page">
      <Link to={homeRoute} className="auth-top-brand" aria-label="Go to home">
        <span className="brand-mark">
          <Icon name="brand" size={18} />
        </span>
        <span>SparkLeaf Child Development Centre</span>
      </Link>
      <div className="auth-card" style={style}>
        <div className="auth-brand">
          <span className="brand-mark">
            <Icon name="brand" size={22} />
          </span>
          <h1>{title}</h1>
          {subtitle && <p>{subtitle}</p>}
        </div>
        {children}
      </div>
    </div>
  );
}

import type { CSSProperties, ReactNode } from "react";
import { Icon } from "./Icon";

interface AuthShellProps {
  title: string;
  subtitle?: string;
  children: ReactNode;
  maxWidth?: number;
}

export function AuthShell({ title, subtitle, children, maxWidth }: AuthShellProps) {
  const style: CSSProperties | undefined = maxWidth ? { maxWidth } : undefined;
  return (
    <div className="auth-page">
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

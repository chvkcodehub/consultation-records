import { Link, NavLink, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../auth/useAuth";
import { Icon, type IconName } from "./Icon";

const adminLinks: { to: string; label: string; icon: IconName; end?: boolean }[] = [
  { to: "/admin", label: "Dashboard", icon: "dashboard", end: true },
  { to: "/admin/consultants", label: "Consultants", icon: "stethoscope" },
  { to: "/admin/consultees", label: "Consultees", icon: "users" },
  { to: "/admin/consultations", label: "Consultations", icon: "calendar" },
  { to: "/admin/goals", label: "Goals", icon: "target" },
  { to: "/admin/reports/consultees", label: "Consultee Sessions", icon: "chart-pie" },
  { to: "/admin/reports/consultants", label: "Consultant Summary", icon: "chart-bar" },
];

const consulteeLinks: { to: string; label: string; icon: IconName; end?: boolean }[] = [
  { to: "/consultee", label: "My Consultations", icon: "calendar", end: true },
  { to: "/consultee/book", label: "Book a Consultation", icon: "book" },
  { to: "/consultee/consultants", label: "Consultants", icon: "stethoscope" },
  { to: "/consultee/profile", label: "My Profile", icon: "user-circle" },
];

const consultantLinks: { to: string; label: string; icon: IconName; end?: boolean }[] = [
  { to: "/consultant", label: "Dashboard", icon: "dashboard", end: true },
  { to: "/consultant/sessions/record", label: "Record a Session", icon: "plus", end: true },
  { to: "/consultant/sessions", label: "Sessions", icon: "calendar", end: true },
  { to: "/consultant/profile", label: "My Profile", icon: "user-circle", end: true },
];

function initialsFor(email: string | null) {
  if (!email) return "?";
  return email.charAt(0).toUpperCase();
}

function displayNameFromEmail(email: string | null) {
  if (!email) return "User";
  const local = email.split("@")[0] ?? "";
  const normalized = local.replace(/[._-]+/g, " ").trim();
  if (!normalized) return "User";
  return normalized
    .split(" ")
    .filter(Boolean)
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}

export function Layout() {
  const { role, email, logout } = useAuth();
  const location = useLocation();
  const links = role === "ADMIN" ? adminLinks : role === "CONSULTANT" ? consultantLinks : consulteeLinks;
  const homeRoute = role === "ADMIN" ? "/admin" : role === "CONSULTANT" ? "/consultant" : "/consultee";
  const roleLabel = role === "ADMIN" ? "Administrator" : role === "CONSULTANT" ? "Consultant" : "Parent / Learner";
  const displayName = displayNameFromEmail(email);
  const showTopbarUser = location.pathname === homeRoute || location.pathname === `${homeRoute}/`;

  return (
    <div className="app-shell">
      <aside className="app-sidebar">
        <Link to={homeRoute} className="app-brand" aria-label="Go to role home">
          <span className="generic-logo app-brand-logo" aria-hidden="true">
            <span className="generic-logo-core" />
          </span>
          <span>SparkLeaf Child Development Centre</span>
        </Link>
        <nav className="app-nav">
          {links.map((link) => (
            <NavLink key={link.to} to={link.to} end={link.end} className={({ isActive }) => (isActive ? "active" : undefined)}>
              <Icon name={link.icon} size={17} />
              <span>{link.label}</span>
            </NavLink>
          ))}
        </nav>
        <div className="app-sidebar-footer">
          <button className="ghost icon-btn" onClick={logout}>
            <Icon name="logout" size={16} />
            Log out
          </button>
        </div>
      </aside>
      <main className="app-content">
        {showTopbarUser ? (
          <div className="app-topbar">
            <div className="app-topbar-user">
              <span className="avatar">{initialsFor(email)}</span>
              <div className="app-topbar-meta">
                <strong>{displayName}</strong>
                <span>{roleLabel}</span>
              </div>
            </div>
          </div>
        ) : null}
        <Outlet />
      </main>
    </div>
  );
}

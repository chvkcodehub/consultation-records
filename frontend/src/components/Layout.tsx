import { NavLink, Outlet } from "react-router-dom";
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

function initialsFor(email: string | null) {
  if (!email) return "?";
  return email.charAt(0).toUpperCase();
}

export function Layout() {
  const { role, email, logout } = useAuth();
  const links = role === "ADMIN" ? adminLinks : consulteeLinks;

  return (
    <div className="app-shell">
      <aside className="app-sidebar">
        <div className="app-brand">
          <span className="brand-mark">
            <Icon name="brand" size={18} />
          </span>
          <span>Consultation Records</span>
        </div>
        <nav className="app-nav">
          {links.map((link) => (
            <NavLink key={link.to} to={link.to} end={link.end}>
              <Icon name={link.icon} size={17} />
              <span>{link.label}</span>
            </NavLink>
          ))}
        </nav>
        <div className="app-sidebar-footer">
          <div className="app-user">
            <span className="avatar">{initialsFor(email)}</span>
            <span className="email">{email}</span>
          </div>
          <button className="ghost icon-btn" onClick={logout}>
            <Icon name="logout" size={16} />
            Log out
          </button>
        </div>
      </aside>
      <main className="app-content">
        <Outlet />
      </main>
    </div>
  );
}

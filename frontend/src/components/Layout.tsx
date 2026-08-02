import { NavLink, Outlet } from "react-router-dom";
import { useAuth } from "../auth/useAuth";

const adminLinks = [
  { to: "/admin", label: "Dashboard", end: true },
  { to: "/admin/consultants", label: "Consultants" },
  { to: "/admin/consultees", label: "Consultees" },
  { to: "/admin/consultations", label: "Consultations" },
  { to: "/admin/goals", label: "Goals" },
  { to: "/admin/reports/consultees", label: "Consultee Sessions Report" },
  { to: "/admin/reports/consultants", label: "Consultant Summary Report" },
];

const consulteeLinks = [
  { to: "/consultee", label: "My Consultations", end: true },
  { to: "/consultee/book", label: "Book a Consultation" },
  { to: "/consultee/consultants", label: "Consultants" },
  { to: "/consultee/profile", label: "My Profile" },
];

export function Layout() {
  const { role, email, logout } = useAuth();
  const links = role === "ADMIN" ? adminLinks : consulteeLinks;

  return (
    <div className="app-shell">
      <header className="app-header">
        <div className="app-brand">Consultation Records</div>
        <nav className="app-nav">
          {links.map((link) => (
            <NavLink key={link.to} to={link.to} end={link.end}>
              {link.label}
            </NavLink>
          ))}
        </nav>
        <div className="app-user">
          <span>{email}</span>
          <button onClick={logout}>Log out</button>
        </div>
      </header>
      <main className="app-content">
        <Outlet />
      </main>
    </div>
  );
}

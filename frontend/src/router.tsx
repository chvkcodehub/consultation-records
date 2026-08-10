import { createBrowserRouter } from "react-router-dom";
import { Layout } from "./components/Layout";
import { RequireRole } from "./auth/RequireRole";
import { HomePage } from "./pages/HomePage";
import { NotFoundPage } from "./pages/NotFoundPage";
import { AdminLoginPage } from "./pages/admin/AdminLoginPage";
import { AdminRegisterPage } from "./pages/admin/AdminRegisterPage";
import { DashboardPage } from "./pages/admin/DashboardPage";
import { ConsultantsPage } from "./pages/admin/ConsultantsPage";
import { ConsulteesPage } from "./pages/admin/ConsulteesPage";
import { ConsultationsPage } from "./pages/admin/ConsultationsPage";
import { GoalsPage } from "./pages/admin/GoalsPage";
import { ReportsConsulteeSessionsPage } from "./pages/admin/ReportsConsulteeSessionsPage";
import { ReportsConsultantSummaryPage } from "./pages/admin/ReportsConsultantSummaryPage";
import { ConsulteeLoginPage } from "./pages/consultee/ConsulteeLoginPage";
import { ConsulteeRegisterPage } from "./pages/consultee/ConsulteeRegisterPage";
import { ConsultantsBrowsePage } from "./pages/consultee/ConsultantsBrowsePage";
import { BookConsultationPage } from "./pages/consultee/BookConsultationPage";
import { MyConsultationsPage } from "./pages/consultee/MyConsultationsPage";
import { ConsultationDetailPage } from "./pages/consultee/ConsultationDetailPage";
import { MyProfilePage } from "./pages/consultee/MyProfilePage";
import { ConsultantLoginPage } from "./pages/consultant/ConsultantLoginPage";
import { ConsultantSessionsPage } from "./pages/consultant/ConsultantSessionsPage";
import { ConsultantDashboardPage } from "./pages/consultant/ConsultantDashboardPage";
import { ConsultantProfilePage } from "./pages/consultant/ConsultantProfilePage";

export const router = createBrowserRouter([
  { path: "/", element: <HomePage /> },
  { path: "/admin/login", element: <AdminLoginPage /> },
  { path: "/admin/register", element: <AdminRegisterPage /> },
  { path: "/consultant/login", element: <ConsultantLoginPage /> },
  { path: "/consultee/login", element: <ConsulteeLoginPage /> },
  { path: "/consultee/register", element: <ConsulteeRegisterPage /> },
  {
    element: <RequireRole role="ADMIN" />,
    children: [
      {
        path: "/admin",
        element: <Layout />,
        children: [
          { index: true, element: <DashboardPage /> },
          { path: "consultants", element: <ConsultantsPage /> },
          { path: "consultees", element: <ConsulteesPage /> },
          { path: "consultations", element: <ConsultationsPage /> },
          { path: "goals", element: <GoalsPage /> },
          { path: "reports/consultees", element: <ReportsConsulteeSessionsPage /> },
          { path: "reports/consultants", element: <ReportsConsultantSummaryPage /> },
        ],
      },
    ],
  },
  {
    element: <RequireRole role="CONSULTANT" />,
    children: [
      {
        path: "/consultant",
        element: <Layout />,
        children: [
          { index: true, element: <ConsultantDashboardPage /> },
          { path: "sessions", element: <ConsultantSessionsPage /> },
          { path: "profile", element: <ConsultantProfilePage /> },
        ],
      },
    ],
  },
  {
    element: <RequireRole role="CONSULTEE" />,
    children: [
      {
        path: "/consultee",
        element: <Layout />,
        children: [
          { index: true, element: <MyConsultationsPage /> },
          { path: "book", element: <BookConsultationPage /> },
          { path: "consultants", element: <ConsultantsBrowsePage /> },
          { path: "consultations/:id", element: <ConsultationDetailPage /> },
          { path: "profile", element: <MyProfilePage /> },
        ],
      },
    ],
  },
  { path: "*", element: <NotFoundPage /> },
]);

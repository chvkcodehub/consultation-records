import { useMemo, useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/useAuth";
import { Icon } from "../components/Icon";
import { authApi } from "../api/authApi";
import { ApiError } from "../api/client";

type HomeTabId = "early-support" | "therapy-programs" | "family-guidance" | "inclusion-readiness";

interface HomeTab {
  id: HomeTabId;
  label: string;
  title: string;
  summary: string;
  points: string[];
  image: string;
}

type LoginRole = "ADMIN" | "CONSULTANT" | "CONSULTEE";

const roleMeta: Record<LoginRole, { title: string; subtitle: string }> = {
  ADMIN: {
    title: "Administrator Sign In",
    subtitle: "Manage learners, consultants, sessions, and reports.",
  },
  CONSULTANT: {
    title: "Consultant Sign In",
    subtitle: "Review consultations and update progress notes.",
  },
  CONSULTEE: {
    title: "Parent / Learner Sign In",
    subtitle: "Track sessions and book new consultations.",
  },
};

const homeTabs: HomeTab[] = [
  {
    id: "early-support",
    label: "Early Support",
    title: "Personalized Foundations for Early Learners",
    summary:
      "Each child begins with a strengths-and-needs profile so classroom goals, sensory plans, and communication supports are tailored from day one.",
    points: [
      "Structured routines with visual schedules and predictable transitions.",
      "Play-based speech and communication milestones with weekly family updates.",
      "Small-group attention building with movement and sensory regulation breaks.",
    ],
    image: "/images/child-development-early.svg",
  },
  {
    id: "therapy-programs",
    label: "Therapy Programs",
    title: "Integrated Therapy Inside the School Day",
    summary:
      "Therapy sessions are blended into learning blocks so children practice skills in real situations, not only in separate rooms.",
    points: [
      "Occupational therapy for fine-motor planning and daily independence.",
      "Speech support for expressive language, social cues, and confidence.",
      "Goal tracking dashboards that highlight progress across every term.",
    ],
    image: "/images/child-development-therapy.svg",
  },
  {
    id: "family-guidance",
    label: "Family Guidance",
    title: "Strong Home-School Partnership",
    summary:
      "Caregivers receive practical coaching and simple routines they can reuse at home, creating consistency for the child.",
    points: [
      "Monthly coaching sessions focused on communication, routines, and behavior support.",
      "Parent circles with facilitator-led topics and resource sharing.",
      "Home activity packs matched to each child's current classroom goals.",
    ],
    image: "/images/child-development-family.svg",
  },
  {
    id: "inclusion-readiness",
    label: "Inclusion Readiness",
    title: "Preparation for Inclusive Classrooms",
    summary:
      "Children build social flexibility and self-advocacy skills to transition smoothly into broader learning environments.",
    points: [
      "Peer collaboration labs with guided turn-taking and teamwork tasks.",
      "Confidence-building communication practice for classroom participation.",
      "Transition planning with receiving educators and families.",
    ],
    image: "/images/child-development-inclusion.svg",
  },
];

export function HomePage() {
  const { isAuthenticated, role, login } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<HomeTabId>("early-support");
  const [loginOpen, setLoginOpen] = useState(false);
  const [panelRole, setPanelRole] = useState<LoginRole | null>(null);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  if (isAuthenticated) {
    return <Navigate to={role === "ADMIN" ? "/admin" : role === "CONSULTANT" ? "/consultant" : "/consultee"} replace />;
  }

  const selectedTab = useMemo(
    () => homeTabs.find((tab) => tab.id === activeTab) ?? homeTabs[0],
    [activeTab],
  );

  const handleRoleSelect = (nextRole: LoginRole) => {
    setLoginOpen(false);
    setPanelRole(nextRole);
    setError(null);
    setPassword("");
  };

  const closePanel = () => {
    setPanelRole(null);
    setError(null);
    setPassword("");
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!panelRole) return;

    setSubmitting(true);
    setError(null);

    try {
      const auth = await authApi.login(email, password);

      if (auth.role !== panelRole) {
        const roleLabels: Record<LoginRole, string> = {
          ADMIN: "administrator",
          CONSULTANT: "consultant",
          CONSULTEE: "parent/learner",
        };
        setError(`This account is not a ${roleLabels[panelRole]} account.`);
        return;
      }

      login(auth, email);

      if (auth.role === "ADMIN") {
        navigate("/admin");
      } else if (auth.role === "CONSULTANT") {
        navigate(auth.passwordChangeRequired ? "/consultant/profile?firstLogin=1" : "/consultant");
      } else {
        navigate("/consultee");
      }
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Login failed");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="landing-page" onClick={() => setLoginOpen(false)}>
      <header className="landing-header">
        <div className="landing-brand">
          <span className="generic-logo" aria-hidden="true">
            <span className="generic-logo-core" />
          </span>
          <div>
            <strong>SparkLeaf Development Centre</strong>
            <p>Special Education and Child Development</p>
          </div>
        </div>

        <div className="landing-login-wrap" onClick={(event) => event.stopPropagation()}>
          <button className="primary" type="button" onClick={() => setLoginOpen((value) => !value)}>
            Login
            <Icon name="arrow-right" size={14} />
          </button>

          {loginOpen ? (
            <div className="login-dropdown" role="menu" aria-label="Role based login">
              <button type="button" className="login-option" onClick={() => handleRoleSelect("ADMIN")}>
                <Icon name="dashboard" size={16} />
                Administrator
              </button>
              <button type="button" className="login-option" onClick={() => handleRoleSelect("CONSULTANT")}>
                <Icon name="stethoscope" size={16} />
                Consultant
              </button>
              <button type="button" className="login-option" onClick={() => handleRoleSelect("CONSULTEE")}>
                <Icon name="user-circle" size={16} />
                Parent / Learner
              </button>
            </div>
          ) : null}

          {panelRole ? (
            <aside className="login-side-panel" role="dialog" aria-modal="true" aria-label="Role login panel">
              <div className="login-side-header">
                <div>
                  <h3>{roleMeta[panelRole].title}</h3>
                  <p>{roleMeta[panelRole].subtitle}</p>
                </div>
                <button type="button" className="ghost" onClick={closePanel}>
                  Close
                </button>
              </div>

              <form onSubmit={handleSubmit}>
                <div className="form-field">
                  <label>Email</label>
                  <div className="field-icon">
                    <Icon name="mail" size={16} />
                    <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                  </div>
                </div>

                <div className="form-field" style={{ marginTop: "0.75rem" }}>
                  <label>Password</label>
                  <div className="field-icon">
                    <Icon name="lock" size={16} />
                    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                  </div>
                </div>

                {error ? <p className="error-text">{error}</p> : null}

                <div className="form-actions">
                  <button className="primary" type="submit" disabled={submitting} style={{ width: "100%", justifyContent: "center" }}>
                    Sign in
                  </button>
                </div>

                {panelRole === "CONSULTEE" ? (
                  <p className="auth-links" style={{ marginTop: "0.85rem" }}>
                    New parent / learner account? <Link to="/consultee/register">Register here</Link>
                  </p>
                ) : null}
              </form>
            </aside>
          ) : null}
        </div>
      </header>

      <main className="landing-main">
        <section className="landing-hero">
          <div className="hero-copy">
            <span className="hero-badge">Inclusive Learning, Practical Support</span>
            <h1>Helping Special Learners Build Skills, Confidence, and Joy</h1>
            <p>
              Our school model combines structured academics, therapy collaboration, and family coaching so children with
              diverse developmental needs can grow with confidence.
            </p>
            <div className="hero-actions">
              <Link to="/consultee/register">
                <button className="primary" type="button">
                  Start With Registration
                </button>
              </Link>
              <a href="#program-tabs" className="hero-link">
                Explore Programs
              </a>
            </div>
          </div>
          <img
            src="/images/child-development-early.svg"
            alt="Children learning with supportive educators"
            className="hero-image"
          />
        </section>

        <section id="program-tabs" className="landing-tabs card-surface">
          <div className="tabs-header">
            <h2>Programs for Special Child Education</h2>
            <p>Choose a focus area to preview how learning plans are delivered.</p>
          </div>

          <div className="tabs-row" role="tablist" aria-label="Special education options">
            {homeTabs.map((tab) => (
              <button
                key={tab.id}
                className={tab.id === activeTab ? "tab-btn active" : "tab-btn"}
                type="button"
                role="tab"
                aria-selected={tab.id === activeTab}
                onClick={() => setActiveTab(tab.id)}
              >
                {tab.label}
              </button>
            ))}
          </div>

          <article className="tab-content" role="tabpanel">
            <div>
              <h3>{selectedTab.title}</h3>
              <p>{selectedTab.summary}</p>
              <ul>
                {selectedTab.points.map((point) => (
                  <li key={point}>{point}</li>
                ))}
              </ul>
            </div>
            <img src={selectedTab.image} alt={selectedTab.label} className="tab-image" />
          </article>
        </section>

        <section className="quick-highlights">
          <article className="highlight-card">
            <h3>Individual Education Plans</h3>
            <p>Targets are set in clear stages and reviewed every month with therapists and families.</p>
          </article>
          <article className="highlight-card">
            <h3>Communication First Classrooms</h3>
            <p>Visual aids, assistive communication, and guided peer interaction are embedded daily.</p>
          </article>
          <article className="highlight-card">
            <h3>Safe Sensory Environments</h3>
            <p>Class spaces are arranged to reduce overwhelm and support attention, calm, and participation.</p>
          </article>
        </section>
      </main>

      <footer className="landing-footer">
        <p>SparkLeaf Development Centre • Inclusive pathways for every learner</p>
      </footer>
    </div>
  );
}

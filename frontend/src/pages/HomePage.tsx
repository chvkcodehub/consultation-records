import { useMemo, useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/useAuth";
import { Icon } from "../components/Icon";
import { authApi } from "../api/authApi";
import { ApiError } from "../api/client";

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

const programs = [
  {
    tag: "Program",
    title: "Speech and Language",
    description: "Communication-first sessions with visual prompts, social cue practice, and expressive language coaching.",
    image: "/images/child-development-early.svg",
  },
  {
    tag: "Program",
    title: "Occupational Skills",
    description: "Fine-motor, sensory regulation, and self-help tasks built into daily classroom activities.",
    image: "/images/child-development-therapy.svg",
  },
  {
    tag: "Program",
    title: "Social Skills Lab",
    description: "Guided peer interactions, turn-taking routines, and confidence-building for inclusive environments.",
    image: "/images/child-development-family.svg",
  },
  {
    tag: "Program",
    title: "Learning Readiness",
    description: "Attention-building routines, early literacy support, and personalized pathways for classroom transition.",
    image: "/images/child-development-inclusion.svg",
  },
];

const timeline = [
  {
    time: "9:00 AM",
    title: "Morning Circle",
    summary: "Visual routine preview, emotional check-in, and communication warm-up.",
  },
  {
    time: "10:30 AM",
    title: "Therapy Block",
    summary: "Small-group speech and occupational sessions with practical classroom integration.",
  },
  {
    time: "12:30 PM",
    title: "Guided Play",
    summary: "Social interaction games focused on turn-taking, shared attention, and collaboration.",
  },
  {
    time: "2:30 PM",
    title: "Parent Brief",
    summary: "Daily progress summary with one home practice routine and next-day focus.",
  },
];

const stories = [
  {
    name: "Parent Story: Aarav",
    quote:
      "In six weeks, Aarav started initiating short conversations at home. The daily parent brief made it easy for us to continue the same routines.",
    detail: "Focus areas: speech confidence, social cues",
    image: "/images/child-development-early.svg",
  },
  {
    name: "Parent Story: Meera",
    quote:
      "The sensory-friendly structure helped Meera settle into class transitions. Her participation in group tasks improved steadily.",
    detail: "Focus areas: sensory regulation, attention",
    image: "/images/child-development-therapy.svg",
  },
  {
    name: "Parent Story: Rihan",
    quote:
      "We appreciated how the team aligned school and home goals. Rihan now follows routines more independently.",
    detail: "Focus areas: independence, routines",
    image: "/images/child-development-family.svg",
  },
];

const events = [
  "Parent Workshop: Building Communication Routines",
  "Inclusive Play Day: Saturday Session",
  "Open Classroom Visit Week",
  "Therapist Q&A for New Families",
];

export function HomePage() {
  const { isAuthenticated, role, login } = useAuth();
  const navigate = useNavigate();
  const [loginOpen, setLoginOpen] = useState(false);
  const [panelRole, setPanelRole] = useState<LoginRole | null>(null);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [storyIndex, setStoryIndex] = useState(0);

  if (isAuthenticated) {
    return <Navigate to={role === "ADMIN" ? "/admin" : role === "CONSULTANT" ? "/consultant" : "/consultee"} replace />;
  }

  const currentStory = useMemo(() => stories[storyIndex] ?? stories[0], [storyIndex]);

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

  const onStoryNav = (delta: number) => {
    setStoryIndex((prev) => {
      const next = prev + delta;
      if (next < 0) return stories.length - 1;
      if (next >= stories.length) return 0;
      return next;
    });
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
        <Link to="/" className="landing-brand" aria-label="Go to home">
          <span className="generic-logo" aria-hidden="true">
            <span className="generic-logo-core" />
          </span>
          <div>
            <strong>SparkLeaf Child Development Centre</strong>
            <p>Special Education and Child Development</p>
          </div>
        </Link>

        <div className="landing-actions" onClick={(event) => event.stopPropagation()}>
          <Link to="/consultee/register" className="header-register">
            Register
          </Link>

          <div className="landing-login-wrap">
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
                    <button
                      className="primary"
                      type="submit"
                      disabled={submitting}
                      style={{ width: "100%", justifyContent: "center" }}
                    >
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
        </div>
      </header>

      <main className="landing-main">
        <section className="landing-hero">
          <div className="hero-copy">
            <span className="hero-badge">Inclusive Learning Spaces</span>
            <h1>Where Every Child Learns Through Confidence, Care, and Play</h1>
            <p>
              SparkLeaf blends therapy-informed teaching, guided social play, and close family partnership to help special
              learners thrive in school and beyond.
            </p>
            <div className="hero-actions">
              <Link to="/consultee/register">
                <button className="primary" type="button">
                  Start With Registration
                </button>
              </Link>
              <a href="#programs" className="hero-link">
                Explore Programs
              </a>
            </div>
          </div>

          <div className="hero-collage">
            <img
              src="/images/child-development-early.svg"
              alt="Children learning with supportive educators"
              className="hero-main-image"
            />
            <img src="/images/child-development-family.svg" alt="Family collaboration" className="hero-floating secondary" />
            <img src="/images/child-development-therapy.svg" alt="Therapy support" className="hero-floating tertiary" />
          </div>
        </section>

        <section id="programs" className="section-block">
          <div className="section-title">
            <h2>How We Help</h2>
            <p>Programs designed for communication, independence, and joyful participation.</p>
          </div>

          <div className="program-grid">
            {programs.map((program) => (
              <article key={program.title} className="program-card">
                <img src={program.image} alt={program.title} className="program-media" />
                <div className="program-copy">
                  <span className="program-tag">{program.tag}</span>
                  <h3>{program.title}</h3>
                  <p>{program.description}</p>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section className="section-block">
          <div className="section-title">
            <h2>A Day at Our Centre</h2>
            <p>Predictable, child-friendly structure that balances therapy, learning, and play.</p>
          </div>

          <div className="timeline-grid">
            {timeline.map((item) => (
              <article key={item.time} className="timeline-item">
                <span className="timeline-dot" aria-hidden="true" />
                <div>
                  <p className="timeline-time">{item.time}</p>
                  <h3>{item.title}</h3>
                  <p>{item.summary}</p>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section className="section-block story-section">
          <div className="section-title">
            <h2>Parent Success Stories</h2>
            <p>Real family experiences with practical progress they can observe at home.</p>
          </div>

          <article className="story-frame">
            <img src={currentStory.image} alt={currentStory.name} className="story-photo" />
            <div>
              <span className="story-pill">{currentStory.name}</span>
              <p>{currentStory.quote}</p>
              <p className="subtitle">{currentStory.detail}</p>

              <div className="story-nav">
                <div className="story-dots" aria-label="Story indicators">
                  {stories.map((story, index) => (
                    <button
                      key={story.name}
                      type="button"
                      className={index === storyIndex ? "story-dot active" : "story-dot"}
                      aria-label={`Show story ${index + 1}`}
                      aria-current={index === storyIndex}
                      onClick={() => setStoryIndex(index)}
                    />
                  ))}
                </div>

                <div className="story-controls">
                  <button type="button" onClick={() => onStoryNav(-1)}>
                    Previous
                  </button>
                  <button className="primary" type="button" onClick={() => onStoryNav(1)}>
                    Next
                  </button>
                </div>
              </div>
            </div>
          </article>
        </section>

        <section className="section-block">
          <div className="section-title">
            <h2>Upcoming Parent and Child Activities</h2>
          </div>

          <div className="events-strip">
            {events.map((event) => (
              <article key={event} className="event-chip">
                {event}
              </article>
            ))}
          </div>
        </section>
      </main>

      <footer className="landing-footer">
        <p>SparkLeaf Child Development Centre • Inclusive pathways for every learner</p>
      </footer>
    </div>
  );
}

import { createContext, useCallback, useEffect, useState, type ReactNode } from "react";
import type { AuthResponse, Role } from "../types";
import { onUnauthorized } from "../api/client";

interface AuthState {
  token: string | null;
  role: Role | null;
  email: string | null;
  consulteeId: string | null;
  consultantId: string | null;
  passwordChangeRequired: boolean;
}

interface AuthContextValue extends AuthState {
  isAuthenticated: boolean;
  login: (auth: AuthResponse, email: string) => void;
  updateEmail: (email: string) => void;
  clearPasswordChangeRequired: () => void;
  logout: () => void;
}

const STORAGE_KEY = "auth";

function loadState(): AuthState {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    return { token: null, role: null, email: null, consulteeId: null, consultantId: null, passwordChangeRequired: false };
  }
  try {
    const parsed = JSON.parse(raw) as Partial<AuthState>;
    return {
      token: parsed.token ?? null,
      role: parsed.role ?? null,
      email: parsed.email ?? null,
      consulteeId: parsed.consulteeId ?? null,
      consultantId: parsed.consultantId ?? null,
      passwordChangeRequired: parsed.passwordChangeRequired ?? false,
    };
  } catch {
    return { token: null, role: null, email: null, consulteeId: null, consultantId: null, passwordChangeRequired: false };
  }
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<AuthState>(loadState);

  const login = useCallback((auth: AuthResponse, email: string) => {
    const next: AuthState = {
      token: auth.token,
      role: auth.role,
      email,
      consulteeId: auth.consulteeId,
      consultantId: auth.consultantId,
      passwordChangeRequired: auth.passwordChangeRequired,
    };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
    localStorage.setItem("token", auth.token);
    setState(next);
  }, []);

  const updateEmail = useCallback((email: string) => {
    setState((prev) => {
      const next = { ...prev, email };
      localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
      return next;
    });
  }, []);

  const clearPasswordChangeRequired = useCallback(() => {
    setState((prev) => {
      if (!prev.passwordChangeRequired) return prev;
      const next = { ...prev, passwordChangeRequired: false };
      localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
      return next;
    });
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(STORAGE_KEY);
    localStorage.removeItem("token");
    setState({ token: null, role: null, email: null, consulteeId: null, consultantId: null, passwordChangeRequired: false });
  }, []);

  useEffect(() => {
    onUnauthorized(() => logout());
  }, [logout]);

  return (
    <AuthContext.Provider
      value={{ ...state, isAuthenticated: !!state.token, login, updateEmail, clearPasswordChangeRequired, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
}

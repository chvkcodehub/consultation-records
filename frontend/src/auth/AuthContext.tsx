import { createContext, useCallback, useEffect, useState, type ReactNode } from "react";
import type { AuthResponse, Role } from "../types";
import { onUnauthorized } from "../api/client";

interface AuthState {
  token: string | null;
  role: Role | null;
  email: string | null;
  consulteeCode: string | null;
}

interface AuthContextValue extends AuthState {
  isAuthenticated: boolean;
  login: (auth: AuthResponse, email: string) => void;
  logout: () => void;
}

const STORAGE_KEY = "auth";

function loadState(): AuthState {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    return { token: null, role: null, email: null, consulteeCode: null };
  }
  try {
    return JSON.parse(raw) as AuthState;
  } catch {
    return { token: null, role: null, email: null, consulteeCode: null };
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
      consulteeCode: auth.consulteeCode,
    };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
    localStorage.setItem("token", auth.token);
    setState(next);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(STORAGE_KEY);
    localStorage.removeItem("token");
    setState({ token: null, role: null, email: null, consulteeCode: null });
  }, []);

  useEffect(() => {
    onUnauthorized(() => logout());
  }, [logout]);

  return (
    <AuthContext.Provider value={{ ...state, isAuthenticated: !!state.token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { consultantsApi } from "../../api/consultantsApi";
import { ApiError } from "../../api/client";
import type { Consultant } from "../../types";

export function ConsultantsBrowsePage() {
  const [consultants, setConsultants] = useState<Consultant[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    consultantsApi
      .list()
      .then(setConsultants)
      .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load consultants"));
  }, []);

  return (
    <div>
      <div className="page-header">
        <h2>Consultants</h2>
      </div>
      {error && <p className="error-text">{error}</p>}
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Speciality</th>
            <th>Qualification</th>
            <th>Experience</th>
            <th>Fee</th>
            <th />
          </tr>
        </thead>
        <tbody>
          {consultants.map((c) => (
            <tr key={c.id}>
              <td>{c.name}</td>
              <td>{c.speciality}</td>
              <td>{c.qualification}</td>
              <td>{c.experienceYears} yrs</td>
              <td>{c.fee}</td>
              <td>
                <Link to={`/consultee/book?consultantCode=${encodeURIComponent(c.code)}`}>
                  <button className="primary">Book</button>
                </Link>
              </td>
            </tr>
          ))}
          {consultants.length === 0 && (
            <tr>
              <td colSpan={6}>No consultants available.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

import React from 'react';

const LABELS = { DEEP_WORK: 'Deep Work', CREATIVE: 'Creative', REVIEW: 'Review' };

/** Simple CSS bar chart of sessions by type. */
const DomainChart = ({ data = {} }) => {
  const entries = Object.entries(data);
  const max = Math.max(1, ...entries.map(([, v]) => v));

  return (
    <div className="card domain-chart">
      <h3>Sessions by Type</h3>
      {entries.length === 0 ? (
        <p className="muted">No data yet.</p>
      ) : (
        entries.map(([key, value]) => (
          <div className="bar-row" key={key}>
            <span className="bar-label">{LABELS[key] || key}</span>
            <div className="bar-track">
              <div className="bar-fill" style={{ width: `${(value / max) * 100}%` }} />
            </div>
            <span className="bar-value">{value}</span>
          </div>
        ))
      )}
    </div>
  );
};

export default DomainChart;

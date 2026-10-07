import React from 'react';

const StatCards = ({ stats }) => {
  const s = stats || {};
  const average = Math.round(s.averageFocusScore ?? 0);

  return (
    <div className="stat-cards">
      <div className="stat-card">
        <h4>Total Sessions</h4>
        <p className="stat-value">{s.totalSessions ?? 0}</p>
      </div>
      <div className="stat-card">
        <h4>Focus Hours</h4>
        <p className="stat-value">{s.totalFocusHours ?? 0}</p>
      </div>
      <div className="stat-card">
        <h4>Average Focus Score</h4>
        <p className="stat-value">{`${average}%`}</p>
      </div>
      <div className="stat-card">
        <h4>Completed</h4>
        <p className="stat-value">{s.completedSessions ?? 0}</p>
      </div>
      <div className="stat-card">
        <h4>Abandoned</h4>
        <p className="stat-value">{s.abandonedSessions ?? 0}</p>
      </div>
    </div>
  );
};

export default StatCards;

import React from 'react';
import EmptyState from '../common/EmptyState';

const RecentActivity = ({ sessions = [] }) => (
  <div className="card recent-activity">
    <h3>Recent Activity</h3>
    {sessions.length === 0 ? (
      <EmptyState message="Schedule your first focus session to get started." />
    ) : (
      <ul>
        {sessions.slice(0, 5).map((s) => (
          <li key={s.id}>
            <span className="activity-title">{s.title}</span>
            <span className={`badge badge-${String(s.status).toLowerCase()}`}>{String(s.status).replace(/_/g, ' ')}</span>
            <span className="muted">{s.plannedStart ? new Date(s.plannedStart).toLocaleDateString() : ''}</span>
          </li>
        ))}
      </ul>
    )}
  </div>
);

export default RecentActivity;

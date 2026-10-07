import React from 'react';

const toMinutes = (time) => {
  if (!time) return 0;
  const [h, m] = String(time).split(':').map(Number);
  return h * 60 + (m || 0);
};

/** Shows how much of the weekly focus capacity is protected. */
const CapacityBar = ({ blocks = [] }) => {
  const total = blocks.reduce((sum, b) => sum + (toMinutes(b.blockEndTime) - toMinutes(b.blockStartTime)), 0);
  const protectedMinutes = blocks
    .filter((b) => b.isProtected)
    .reduce((sum, b) => sum + (toMinutes(b.blockEndTime) - toMinutes(b.blockStartTime)), 0);
  const percent = total === 0 ? 0 : Math.round((protectedMinutes / total) * 100);

  return (
    <div className="capacity-bar">
      <div className="capacity-label">
        <span>Protected capacity</span>
        <span>
          {(protectedMinutes / 60).toFixed(1)}h of {(total / 60).toFixed(1)}h ({percent}%)
        </span>
      </div>
      <div className="capacity-track">
        <div className="capacity-fill" style={{ width: `${percent}%` }} />
      </div>
    </div>
  );
};

export default CapacityBar;

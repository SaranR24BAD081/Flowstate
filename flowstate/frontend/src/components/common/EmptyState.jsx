import React from 'react';

const EmptyState = ({ title = 'Nothing here yet', message }) => (
  <div className="empty-state">
    <h4>{title}</h4>
    {message && <p>{message}</p>}
  </div>
);

export default EmptyState;

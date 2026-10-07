import React from 'react';

/** Global error display. Renders nothing when there is no error. */
const ErrorHandler = ({ error, message, onClear }) => {
  const text = error || message;
  if (!text) return null;

  return (
    <div className="error-handler" role="alert">
      <div className="error-header">
        <h3>Error Occurred</h3>
        <button type="button" className="close-btn" aria-label="Close" onClick={onClear}>
          ×
        </button>
      </div>
      <div className="error-body">
        <p>{text}</p>
      </div>
      <div className="error-footer">
        <button type="button" className="secondary-btn" onClick={onClear}>
          Dismiss
        </button>
      </div>
    </div>
  );
};

export default ErrorHandler;

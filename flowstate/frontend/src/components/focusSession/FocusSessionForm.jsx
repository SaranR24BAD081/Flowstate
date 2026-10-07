import React, { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { clearError, createSession, updateSession } from '../../store/slices/focusSessionSlice';

const pad = (n) => String(n).padStart(2, '0');
const toLocalInput = (date) =>
  `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;

const FocusSessionForm = ({ session = null, onClose }) => {
  const dispatch = useDispatch();
  const { error } = useSelector((state) => state.focusSessions);
  const isEdit = Boolean(session && session.id);

  const [title, setTitle] = useState(session?.title || '');
  const [plannedStart, setPlannedStart] = useState(
    session?.plannedStart ? String(session.plannedStart).slice(0, 16) : toLocalInput(new Date())
  );
  const [plannedEnd, setPlannedEnd] = useState(
    session?.plannedEnd ? String(session.plannedEnd).slice(0, 16) : toLocalInput(new Date(Date.now() + 60 * 60 * 1000))
  );
  const [sessionType, setSessionType] = useState(session?.sessionType || 'DEEP_WORK');
  const [notes, setNotes] = useState(session?.notes || '');
  const [localError, setLocalError] = useState('');
  const [success, setSuccess] = useState('');

  const cardRef = useRef(null);
  const closeTimer = useRef(null);

  useEffect(() => {
    if (cardRef.current && typeof cardRef.current.scrollIntoView === 'function') {
      cardRef.current.scrollIntoView();
    }
    return () => {
      if (closeTimer.current) clearTimeout(closeTimer.current);
      dispatch(clearError());
    };
  }, [dispatch]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLocalError('');
    setSuccess('');

    const start = new Date(plannedStart);
    const end = new Date(plannedEnd);
    const minutes = (end - start) / 60000;

    if (end <= start) {
      setLocalError('Planned end time must be after planned start time.');
      return;
    }
    if (minutes < 15) {
      setLocalError('Session must be at least 15 minutes long.');
      return;
    }
    if (minutes > 480 && !window.confirm('This session is longer than 8 hours. Do you want to continue?')) {
      return;
    }

    const data = { title, plannedStart, plannedEnd, sessionType, notes };
    const action = isEdit ? updateSession({ id: session.id, data }) : createSession(data);
    const thunk = isEdit ? updateSession : createSession;
    const result = await dispatch(action);

    if (thunk.fulfilled.match(result)) {
      setSuccess(isEdit ? 'FocusSession updated successfully.' : 'FocusSession scheduled successfully.');
      closeTimer.current = setTimeout(() => onClose && onClose(), 2000);
    }
  };

  const shownError = localError || error;

  return (
    <div className="modal-overlay">
      <div className="modal-card" ref={cardRef}>
        <div className="modal-header">
          <h2>{isEdit ? 'Edit Session' : 'Schedule New Session'}</h2>
          <button type="button" className="close-btn" aria-label="Close" onClick={onClose}>
            ×
          </button>
        </div>

        {shownError && <div className="error-banner">{shownError}</div>}
        {success && <div className="success-banner">{success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="title">Session Title *</label>
            <input
              type="text"
              id="title"
              placeholder="e.g. Frontend Architecture Refactor"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              maxLength={200}
              required
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="plannedStart">Planned Start *</label>
              <input
                type="datetime-local"
                id="plannedStart"
                value={plannedStart}
                onChange={(e) => setPlannedStart(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="plannedEnd">Planned End *</label>
              <input
                type="datetime-local"
                id="plannedEnd"
                value={plannedEnd}
                onChange={(e) => setPlannedEnd(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="sessionType">Session Type *</label>
            <select id="sessionType" value={sessionType} onChange={(e) => setSessionType(e.target.value)}>
              <option value="DEEP_WORK">Deep Work</option>
              <option value="CREATIVE">Creative</option>
              <option value="REVIEW">Review</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="notes">Notes</label>
            <textarea
              id="notes"
              placeholder="Context or specific objectives..."
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              rows={3}
            />
          </div>

          <button type="submit" className="primary-btn full-width">
            {isEdit ? 'Update Session' : 'Schedule Session'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default FocusSessionForm;

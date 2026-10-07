import React, { useCallback, useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import SearchFilterBar from '../common/SearchFilterBar';
import FocusSessionForm from './FocusSessionForm';
import {
  abandonSession,
  completeSession,
  deleteSession,
  fetchAllSessions,
  fetchMySessions,
  setFilterByStatus,
  setSearchQuery,
  startSession,
} from '../../store/slices/focusSessionSlice';

const STATUSES = ['SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'ABANDONED'];

const FocusSessionList = () => {
  const dispatch = useDispatch();
  const user = useSelector((state) => state.auth.user);
  const { items, totalPages, currentPage, filterByStatus, searchQuery, error } = useSelector(
    (state) => state.focusSessions
  );
  const isPractitioner = user?.role === 'PRACTITIONER';

  const [showForm, setShowForm] = useState(false);
  const [editing, setEditing] = useState(null);
  const [message, setMessage] = useState('');
  const [abandonTarget, setAbandonTarget] = useState(null);
  const [abandonReason, setAbandonReason] = useState('');

  const load = useCallback(
    (page = 0) => {
      const params = {
        page,
        size: 10,
        status: filterByStatus || undefined,
        title: searchQuery || undefined,
      };
      dispatch(isPractitioner ? fetchMySessions(params) : fetchAllSessions(params));
    },
    [dispatch, isPractitioner, filterByStatus, searchQuery]
  );

  useEffect(() => {
    load(0);
  }, [load]);

  const handleDelete = async (session) => {
    if (!window.confirm(`Delete "${session.title}"?`)) return;
    const result = await dispatch(deleteSession(session.id));
    if (deleteSession.fulfilled.match(result)) {
      setMessage('FocusSession deleted successfully.');
    }
  };

  const handleAbandonConfirm = async () => {
    const result = await dispatch(abandonSession({ id: abandonTarget.id, reason: abandonReason }));
    if (abandonSession.fulfilled.match(result)) {
      setAbandonTarget(null);
      setAbandonReason('');
    }
  };

  const openCreate = () => {
    setEditing(null);
    setMessage('');
    setShowForm(true);
  };

  const closeForm = () => {
    setShowForm(false);
    setEditing(null);
  };

  return (
    <div className="page-container">
      <h1>{isPractitioner ? 'My Focus Sessions' : 'All Sessions'}</h1>

      {isPractitioner && (
        <button type="button" className="primary-btn" onClick={openCreate}>
          + Schedule Session
        </button>
      )}

      {error && <div className="alert error">{error}</div>}
      {message && <div className="alert success">{message}</div>}

      <SearchFilterBar
        placeholder="Search sessions by title..."
        searchValue={searchQuery}
        onSearchChange={(v) => dispatch(setSearchQuery(v))}
        statusValue={filterByStatus}
        onStatusChange={(v) => dispatch(setFilterByStatus(v))}
        statusOptions={STATUSES}
      />

      <div className="card table-card">
        <table className="data-table">
          <thead>
            <tr>
              <th>Title</th>
              <th>Type</th>
              <th>Scheduled</th>
              <th>Status</th>
              <th>Score</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {items.length === 0 && (
              <tr>
                <td colSpan={6} className="muted center">
                  No sessions found.
                </td>
              </tr>
            )}
            {items.map((s) => (
              <tr key={s.id}>
                <td className="strong">{s.title}</td>
                <td>{String(s.sessionType).replace(/_/g, ' ')}</td>
                <td>{s.plannedStart ? new Date(s.plannedStart).toLocaleString() : '-'}</td>
                <td>
                  <span className={`badge badge-${String(s.status).toLowerCase()}`}>
                    {String(s.status).replace(/_/g, ' ')}
                  </span>
                </td>
                <td>{s.focusScore != null ? `${s.focusScore}%` : '-'}</td>
                <td className="actions">
                  {isPractitioner && s.status === 'SCHEDULED' && (
                    <>
                      <button type="button" className="success-btn" onClick={() => dispatch(startSession(s.id))}>
                        Start
                      </button>
                      <button
                        type="button"
                        className="secondary-btn"
                        onClick={() => {
                          setEditing(s);
                          setMessage('');
                          setShowForm(true);
                        }}
                      >
                        Edit
                      </button>
                      <button type="button" className="secondary-btn" onClick={() => handleDelete(s)}>
                        Delete
                      </button>
                    </>
                  )}
                  {isPractitioner && s.status === 'IN_PROGRESS' && (
                    <>
                      <button type="button" className="success-btn" onClick={() => dispatch(completeSession(s.id))}>
                        Complete
                      </button>
                      <button type="button" className="danger-btn" onClick={() => setAbandonTarget(s)}>
                        Abandon
                      </button>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <div className="pagination">
          <button type="button" className="secondary-btn" disabled={currentPage <= 0} onClick={() => load(currentPage - 1)}>
            Prev
          </button>
          <span>
            Page {currentPage + 1} of {Math.max(totalPages, 1)}
          </span>
          <button
            type="button"
            className="secondary-btn"
            disabled={currentPage + 1 >= totalPages}
            onClick={() => load(currentPage + 1)}
          >
            Next
          </button>
        </div>
      </div>

      {showForm && <FocusSessionForm session={editing} onClose={closeForm} />}

      {abandonTarget && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <h2>Abandon Session</h2>
              <button type="button" className="close-btn" aria-label="Close" onClick={() => setAbandonTarget(null)}>
                ×
              </button>
            </div>
            <p className="muted">Why are you abandoning "{abandonTarget.title}"?</p>
            <div className="form-group">
              <label htmlFor="abandonReason">Reason</label>
              <textarea
                id="abandonReason"
                rows={3}
                value={abandonReason}
                onChange={(e) => setAbandonReason(e.target.value)}
              />
            </div>
            <button type="button" className="danger-btn full-width" onClick={handleAbandonConfirm}>
              Confirm Abandon
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default FocusSessionList;

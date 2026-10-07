import React, { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  clearRecommendationError,
  fetchPractitioners,
  issueRecommendation,
} from '../../store/slices/coachRecommendationSlice';

const CoachRecommendationForm = ({ onClose }) => {
  const dispatch = useDispatch();
  const { practitioners, error } = useSelector((state) => state.coachRecommendations);

  const [practitionerId, setPractitionerId] = useState('');
  const [recommendationText, setText] = useState('');
  const [priority, setPriority] = useState('MEDIUM');
  const [success, setSuccess] = useState('');
  const timer = useRef(null);

  useEffect(() => {
    dispatch(fetchPractitioners());
    return () => {
      if (timer.current) clearTimeout(timer.current);
      dispatch(clearRecommendationError());
    };
  }, [dispatch]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const result = await dispatch(
      issueRecommendation({ practitionerId: Number(practitionerId), recommendationText, priority })
    );
    if (issueRecommendation.fulfilled.match(result)) {
      setSuccess('Recommendation issued successfully.');
      timer.current = setTimeout(() => onClose(), 1500);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <h2>Issue Recommendation</h2>
          <button type="button" className="close-btn" aria-label="Close" onClick={onClose}>
            ×
          </button>
        </div>
        {error && <div className="error-banner">{error}</div>}
        {success && <div className="success-banner">{success}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="practitionerId">Practitioner *</label>
            <select id="practitionerId" value={practitionerId} onChange={(e) => setPractitionerId(e.target.value)} required>
              <option value="">Select a practitioner</option>
              {practitioners.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.fullName} ({p.username})
                </option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label htmlFor="recommendationText">Recommendation *</label>
            <textarea
              id="recommendationText"
              rows={4}
              minLength={10}
              placeholder="At least 10 characters..."
              value={recommendationText}
              onChange={(e) => setText(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="priority">Priority *</label>
            <select id="priority" value={priority} onChange={(e) => setPriority(e.target.value)}>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>
          <button type="submit" className="primary-btn full-width">
            Issue Recommendation
          </button>
        </form>
      </div>
    </div>
  );
};

export default CoachRecommendationForm;

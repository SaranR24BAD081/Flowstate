import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import EmptyState from '../common/EmptyState';
import CoachRecommendationForm from './CoachRecommendationForm';
import {
  acknowledgeRecommendation,
  dismissRecommendation,
  fetchIssuedRecommendations,
  fetchPendingRecommendations,
} from '../../store/slices/coachRecommendationSlice';

/**
 * Coaches see the recommendations they issued (and can issue new ones);
 * practitioners see their pending recommendations and can acknowledge / dismiss them.
 */
const CoachRecommendationList = ({ embedded = false }) => {
  const dispatch = useDispatch();
  const user = useSelector((state) => state.auth.user);
  const { pending, issued, error } = useSelector((state) => state.coachRecommendations);
  const [showForm, setShowForm] = useState(false);

  const isCoach = user?.role === 'FLOW_COACH';
  const isPractitioner = user?.role === 'PRACTITIONER';

  useEffect(() => {
    if (isCoach) dispatch(fetchIssuedRecommendations({ page: 0, size: 20 }));
    if (isPractitioner) dispatch(fetchPendingRecommendations());
  }, [dispatch, isCoach, isPractitioner]);

  const rows = isCoach ? issued : pending;

  return (
    <div className={embedded ? 'card' : 'page-container'}>
      {embedded ? <h3>Coach Recommendations</h3> : <h1>Recommendations</h1>}

      {isCoach && (
        <button type="button" className="primary-btn" onClick={() => setShowForm(true)}>
          + Issue Recommendation
        </button>
      )}

      {error && <div className="alert error">{error}</div>}

      {rows.length === 0 ? (
        <EmptyState
          title="No recommendations"
          message={isCoach ? 'Issue your first recommendation to a practitioner.' : 'You are all caught up.'}
        />
      ) : (
        <ul className="recommendation-list">
          {rows.map((r) => (
            <li key={r.id} className="recommendation-item">
              <div>
                <span className={`badge badge-priority-${String(r.priority).toLowerCase()}`}>{r.priority}</span>
                <span className="muted"> {isCoach ? `For ${r.practitionerName}` : `From ${r.coachName}`}</span>
              </div>
              <p>{r.recommendationText}</p>
              {isCoach ? (
                <span className={`badge badge-${String(r.status).toLowerCase()}`}>{r.status}</span>
              ) : (
                <div className="actions">
                  <button type="button" className="success-btn" onClick={() => dispatch(acknowledgeRecommendation(r.id))}>
                    Acknowledge
                  </button>
                  <button type="button" className="secondary-btn" onClick={() => dispatch(dismissRecommendation(r.id))}>
                    Dismiss
                  </button>
                </div>
              )}
            </li>
          ))}
        </ul>
      )}

      {showForm && <CoachRecommendationForm onClose={() => setShowForm(false)} />}
    </div>
  );
};

export default CoachRecommendationList;

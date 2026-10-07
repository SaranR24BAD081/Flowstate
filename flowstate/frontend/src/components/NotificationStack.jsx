import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchPendingRecommendations } from '../store/slices/coachRecommendationSlice';

/** Floating toasts. Currently tells practitioners about pending coach recommendations. */
const NotificationStack = () => {
  const dispatch = useDispatch();
  const user = useSelector((state) => state.auth.user);
  const pending = useSelector((state) => state.coachRecommendations.pending);
  const [dismissed, setDismissed] = useState(false);

  const isPractitioner = user?.role === 'PRACTITIONER';

  useEffect(() => {
    if (isPractitioner) dispatch(fetchPendingRecommendations());
  }, [dispatch, isPractitioner]);

  if (!isPractitioner || dismissed || !pending || pending.length === 0) return null;

  return (
    <div className="notification-stack">
      <div className="notification info">
        <span>
          You have {pending.length} pending coach recommendation{pending.length > 1 ? 's' : ''}.
        </span>
        <button type="button" aria-label="Dismiss notification" onClick={() => setDismissed(true)}>
          ×
        </button>
      </div>
    </div>
  );
};

export default NotificationStack;

import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Navigate, Route, Routes } from 'react-router-dom';
import Login from './components/Login';
import Navbar from './components/layout/Navbar';
import NotificationStack from './components/NotificationStack';
import StatCards from './components/dashboard/StatCards';
import RecentActivity from './components/dashboard/RecentActivity';
import DomainChart from './components/dashboard/DomainChart';
import FocusSessionList from './components/focusSession/FocusSessionList';
import FocusBlockList from './components/focusBlock/FocusBlockList';
import CoachRecommendationList from './components/coachRecommendation/CoachRecommendationList';
import { fetchMySessions, fetchStats } from './store/slices/focusSessionSlice';

const ProtectedRoute = ({ roles, children }) => {
  const user = useSelector((state) => state.auth.user);
  if (!user) return <Navigate to="/login" replace />;
  if (roles && !roles.includes(user.role)) return <Navigate to="/" replace />;
  return children;
};

const Home = () => {
  const dispatch = useDispatch();
  const user = useSelector((state) => state.auth.user);
  const { stats, items } = useSelector((state) => state.focusSessions);

  const isPractitioner = user?.role === 'PRACTITIONER';
  const hasStats = isPractitioner || user?.role === 'PLATFORM_ADMIN';

  useEffect(() => {
    if (hasStats) dispatch(fetchStats());
    if (isPractitioner) dispatch(fetchMySessions({ page: 0, size: 5 }));
  }, [dispatch, hasStats, isPractitioner]);

  return (
    <div className="page-container">
      <h1>Hello, {user.fullName}!</h1>
      <p className="muted">Here is a snapshot of your focus practice.</p>

      {hasStats && (
        <>
          <StatCards stats={stats} />
          <div className="dashboard-grid">
            {isPractitioner && <RecentActivity sessions={items} />}
            <DomainChart data={stats?.sessionsByType || {}} />
          </div>
        </>
      )}

      {isPractitioner && <CoachRecommendationList embedded />}
    </div>
  );
};

const App = () => (
  <div className="app">
    <Navbar />
    <NotificationStack />
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<ProtectedRoute><Home /></ProtectedRoute>} />
      <Route path="/sessions" element={<ProtectedRoute><FocusSessionList /></ProtectedRoute>} />
      <Route
        path="/blocks"
        element={<ProtectedRoute roles={['PRACTITIONER']}><FocusBlockList /></ProtectedRoute>}
      />
      <Route
        path="/recommendations"
        element={<ProtectedRoute roles={['FLOW_COACH']}><CoachRecommendationList /></ProtectedRoute>}
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  </div>
);

export default App;

import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link, useNavigate } from 'react-router-dom';
import { logout } from '../../store/slices/authSlice';
import { AUTH_STORAGE_KEYS } from '../../services/api';

const Navbar = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const user = useSelector((state) => state.auth.user);

  if (!user) return null;

  const handleLogout = () => {
    AUTH_STORAGE_KEYS.forEach((key) => localStorage.removeItem(key));
    dispatch(logout());
    navigate('/login');
  };

  const { role } = user;

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/">FlowState</Link>
      </div>

      <div className="navbar-links">
        <Link to="/">Home</Link>
        {role === 'PRACTITIONER' && (
          <>
            <Link to="/sessions">Sessions</Link>
            <Link to="/blocks">Focus Blocks</Link>
          </>
        )}
        {(role === 'FLOW_COACH' || role === 'PLATFORM_ADMIN') && <Link to="/sessions">All Sessions</Link>}
        {role === 'FLOW_COACH' && <Link to="/recommendations">Recommendations</Link>}
      </div>

      <div className="navbar-user">
        <span className="welcome">Welcome back, {user.fullName}!</span>
        <button type="button" className="logout-btn" onClick={handleLogout}>
          Logout
        </button>
      </div>
    </nav>
  );
};

export default Navbar;

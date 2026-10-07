import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { clearAuthError, loginUser, registerUser } from '../store/slices/authSlice';

const Login = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user, loading, error } = useSelector((state) => state.auth);

  const [isRegistering, setIsRegistering] = useState(false);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [regUsername, setRegUsername] = useState('');
  const [regEmail, setRegEmail] = useState('');
  const [regFullName, setRegFullName] = useState('');
  const [regPassword, setRegPassword] = useState('');
  const [regRole, setRegRole] = useState('PRACTITIONER');

  useEffect(() => {
    if (user) navigate('/');
  }, [user, navigate]);

  useEffect(() => () => dispatch(clearAuthError()), [dispatch]);

  const toggleMode = () => {
    dispatch(clearAuthError());
    setIsRegistering((prev) => !prev);
  };

  const handleLogin = (e) => {
    e.preventDefault();
    dispatch(loginUser({ username, password }));
  };

  const handleRegister = (e) => {
    e.preventDefault();
    dispatch(
      registerUser({
        username: regUsername,
        email: regEmail,
        fullName: regFullName,
        password: regPassword,
        role: regRole,
      })
    );
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1 className="auth-title">FlowState</h1>
        <p className="auth-subtitle">Optimize your deep work sessions</p>

        {error && <div className="error-banner">{error}</div>}

        {!isRegistering ? (
          <form onSubmit={handleLogin}>
            <div className="form-group">
              <label htmlFor="username">Username</label>
              <input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="password">Password</label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <button type="submit" className="primary-btn full-width" disabled={loading}>
              {loading ? 'Logging in...' : 'Login'}
            </button>
            <button type="button" className="toggle-link" onClick={toggleMode}>
              Don't have an account? Register
            </button>
          </form>
        ) : (
          <form onSubmit={handleRegister}>
            <div className="form-group">
              <label htmlFor="reg-username">Username *</label>
              <input
                id="reg-username"
                type="text"
                placeholder="Choose a username"
                value={regUsername}
                onChange={(e) => setRegUsername(e.target.value)}
                minLength={3}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="reg-email">Email *</label>
              <input
                id="reg-email"
                type="email"
                placeholder="your@email.com"
                value={regEmail}
                onChange={(e) => setRegEmail(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="reg-fullname">Full Name *</label>
              <input
                id="reg-fullname"
                type="text"
                placeholder="John Doe"
                value={regFullName}
                onChange={(e) => setRegFullName(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="reg-password">Password *</label>
              <input
                id="reg-password"
                type="password"
                placeholder="At least 8 characters"
                value={regPassword}
                onChange={(e) => setRegPassword(e.target.value)}
                minLength={8}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="reg-role">Role *</label>
              <select id="reg-role" value={regRole} onChange={(e) => setRegRole(e.target.value)}>
                <option value="PRACTITIONER">Practitioner</option>
                <option value="FLOW_COACH">Flow Coach</option>
              </select>
            </div>
            <button type="submit" className="primary-btn full-width" disabled={loading}>
              {loading ? 'Registering...' : 'Register'}
            </button>
            <button type="button" className="toggle-link" onClick={toggleMode}>
              Already have an account? Login
            </button>
          </form>
        )}
      </div>
    </div>
  );
};

export default Login;

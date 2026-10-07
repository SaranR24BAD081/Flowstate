import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import authService from '../../services/authService';
import { AUTH_STORAGE_KEYS, getErrorMessage } from '../../services/api';

const clearStorage = () => AUTH_STORAGE_KEYS.forEach((key) => localStorage.removeItem(key));

const persistSession = (data) => {
  localStorage.setItem('flowstate_access_token', data.accessToken);
  localStorage.setItem('flowstate_refresh_token', data.refreshToken);
  localStorage.setItem('flowstate_user_id', String(data.userId));
  localStorage.setItem('flowstate_username', data.username);
  localStorage.setItem('flowstate_full_name', data.fullName);
  localStorage.setItem('flowstate_role', data.role);
};

const hydrateUser = () => {
  const token = localStorage.getItem('flowstate_access_token');
  if (!token) return null;
  return {
    token,
    userId: localStorage.getItem('flowstate_user_id'),
    username: localStorage.getItem('flowstate_username'),
    fullName: localStorage.getItem('flowstate_full_name'),
    role: localStorage.getItem('flowstate_role'),
  };
};

export const loginUser = createAsyncThunk('auth/login', async (credentials, { rejectWithValue }) => {
  try {
    const res = await authService.login(credentials);
    return res.data;
  } catch (err) {
    return rejectWithValue(getErrorMessage(err, 'Invalid username or password'));
  }
});

export const registerUser = createAsyncThunk('auth/register', async (payload, { rejectWithValue }) => {
  try {
    const res = await authService.register(payload);
    return res.data;
  } catch (err) {
    return rejectWithValue(getErrorMessage(err, 'Registration failed'));
  }
});

const toUser = (data) => ({
  token: data.accessToken,
  userId: data.userId,
  username: data.username,
  fullName: data.fullName,
  role: data.role,
});

const authSlice = createSlice({
  name: 'auth',
  initialState: { user: hydrateUser(), loading: false, error: null },
  reducers: {
    logout: (state) => {
      state.user = null;
      state.error = null;
      clearStorage();
    },
    clearAuthError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    [loginUser, registerUser].forEach((thunk) => {
      builder
        .addCase(thunk.pending, (state) => {
          state.loading = true;
          state.error = null;
        })
        .addCase(thunk.fulfilled, (state, action) => {
          state.loading = false;
          state.user = toUser(action.payload);
          persistSession(action.payload);
        })
        .addCase(thunk.rejected, (state, action) => {
          state.loading = false;
          state.error = action.payload;
        });
    });

    // Any rejected action carrying an "Unauthorized"/401 payload ends the session.
    builder.addMatcher(
      (action) =>
        action.type.endsWith('/rejected') &&
        (action.payload === 'Unauthorized' || action.payload === '401' || action.payload === 401),
      (state) => {
        state.user = null;
        localStorage.removeItem('flowstate_access_token');
      }
    );
  },
});

export const { logout, clearAuthError } = authSlice.actions;
export default authSlice.reducer;

import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import distractionLogService from '../../services/distractionLogService';
import { getErrorMessage } from '../../services/api';

const makeThunk = (type, call, fallback) =>
  createAsyncThunk(`distractionLogs/${type}`, async (arg, { rejectWithValue }) => {
    try {
      const res = await call(arg);
      return res.data;
    } catch (err) {
      return rejectWithValue(getErrorMessage(err, fallback));
    }
  });

export const logDistraction = makeThunk(
  'logDistraction', (data) => distractionLogService.log(data), 'Unable to log distraction');
export const fetchLogsForSession = makeThunk(
  'fetchLogsForSession', (sessionId) => distractionLogService.getForSession(sessionId), 'Failed to fetch distractions');
export const fetchBreakdown = makeThunk(
  'fetchBreakdown', () => distractionLogService.getBreakdown(), 'Failed to fetch distraction breakdown');
export const fetchTrend = makeThunk(
  'fetchTrend', () => distractionLogService.getTrend(), 'Failed to fetch distraction trend');

const distractionLogSlice = createSlice({
  name: 'distractionLogs',
  initialState: { items: [], breakdown: null, trend: [], loading: false, error: null },
  reducers: {
    clearDistractionError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchLogsForSession.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(logDistraction.fulfilled, (state, action) => {
        state.loading = false;
        state.items.unshift(action.payload);
      })
      .addCase(fetchBreakdown.fulfilled, (state, action) => {
        state.loading = false;
        state.breakdown = action.payload;
      })
      .addCase(fetchTrend.fulfilled, (state, action) => {
        state.loading = false;
        state.trend = action.payload;
      })
      .addMatcher(
        (a) => a.type.startsWith('distractionLogs/') && a.type.endsWith('/pending'),
        (state) => {
          state.loading = true;
          state.error = null;
        }
      )
      .addMatcher(
        (a) => a.type.startsWith('distractionLogs/') && a.type.endsWith('/rejected'),
        (state, action) => {
          state.loading = false;
          state.error = action.payload || 'Something went wrong';
        }
      );
  },
});

export const { clearDistractionError } = distractionLogSlice.actions;
export default distractionLogSlice.reducer;

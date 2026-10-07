import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import sessionGoalService from '../../services/sessionGoalService';
import { getErrorMessage } from '../../services/api';

const makeThunk = (type, call, fallback) =>
  createAsyncThunk(`sessionGoals/${type}`, async (arg, { rejectWithValue }) => {
    try {
      const res = await call(arg);
      return res.data;
    } catch (err) {
      return rejectWithValue(getErrorMessage(err, fallback));
    }
  });

export const attachGoal = makeThunk('attachGoal', (data) => sessionGoalService.attach(data), 'Unable to attach goal');
export const fetchGoalsForSession = makeThunk(
  'fetchGoalsForSession', (sessionId) => sessionGoalService.getForSession(sessionId), 'Failed to fetch goals');
export const markGoalOutcome = makeThunk(
  'markGoalOutcome', ({ id, achievedMinutes }) => sessionGoalService.markOutcome(id, achievedMinutes),
  'Unable to record goal outcome');
export const fetchCompletionRate = makeThunk(
  'fetchCompletionRate', () => sessionGoalService.getCompletionRate(), 'Failed to fetch completion rate');

const sessionGoalSlice = createSlice({
  name: 'sessionGoals',
  initialState: { items: [], completionRate: null, loading: false, error: null },
  reducers: {
    clearGoalError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchGoalsForSession.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(attachGoal.fulfilled, (state, action) => {
        state.loading = false;
        state.items.push(action.payload);
      })
      .addCase(markGoalOutcome.fulfilled, (state, action) => {
        state.loading = false;
        const i = state.items.findIndex((g) => g.id === action.payload.id);
        if (i >= 0) state.items[i] = action.payload;
      })
      .addCase(fetchCompletionRate.fulfilled, (state, action) => {
        state.loading = false;
        state.completionRate = action.payload;
      })
      .addMatcher(
        (a) => a.type.startsWith('sessionGoals/') && a.type.endsWith('/pending'),
        (state) => {
          state.loading = true;
          state.error = null;
        }
      )
      .addMatcher(
        (a) => a.type.startsWith('sessionGoals/') && a.type.endsWith('/rejected'),
        (state, action) => {
          state.loading = false;
          state.error = action.payload || 'Something went wrong';
        }
      );
  },
});

export const { clearGoalError } = sessionGoalSlice.actions;
export default sessionGoalSlice.reducer;

import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import coachRecommendationService from '../../services/coachRecommendationService';
import { getErrorMessage } from '../../services/api';

const makeThunk = (type, call, fallback) =>
  createAsyncThunk(`coachRecommendations/${type}`, async (arg, { rejectWithValue }) => {
    try {
      const res = await call(arg);
      return res.data;
    } catch (err) {
      return rejectWithValue(getErrorMessage(err, fallback));
    }
  });

export const fetchPendingRecommendations = makeThunk(
  'fetchPending', () => coachRecommendationService.getMyPending(), 'Failed to fetch recommendations');
export const fetchIssuedRecommendations = makeThunk(
  'fetchIssued', (params) => coachRecommendationService.getMyIssued(params), 'Failed to fetch recommendations');
export const issueRecommendation = makeThunk(
  'issue', (data) => coachRecommendationService.issue(data), 'Unable to issue recommendation');
export const acknowledgeRecommendation = makeThunk(
  'acknowledge', (id) => coachRecommendationService.acknowledge(id), 'Unable to acknowledge recommendation');
export const dismissRecommendation = makeThunk(
  'dismiss', (id) => coachRecommendationService.dismiss(id), 'Unable to dismiss recommendation');
export const fetchPractitioners = makeThunk(
  'fetchPractitioners', () => coachRecommendationService.getPractitioners(), 'Failed to load practitioners');

const coachRecommendationSlice = createSlice({
  name: 'coachRecommendations',
  initialState: { pending: [], issued: [], practitioners: [], totalPages: 0, currentPage: 0, loading: false, error: null },
  reducers: {
    clearRecommendationError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchPendingRecommendations.fulfilled, (state, action) => {
        state.loading = false;
        state.pending = action.payload;
      })
      .addCase(fetchIssuedRecommendations.fulfilled, (state, action) => {
        state.loading = false;
        state.issued = action.payload.content || [];
        state.totalPages = action.payload.totalPages ?? 0;
        state.currentPage = action.payload.number ?? 0;
      })
      .addCase(issueRecommendation.fulfilled, (state, action) => {
        state.loading = false;
        state.issued.unshift(action.payload);
      })
      .addCase(fetchPractitioners.fulfilled, (state, action) => {
        state.loading = false;
        state.practitioners = action.payload;
      });
    [acknowledgeRecommendation, dismissRecommendation].forEach((thunk) => {
      builder.addCase(thunk.fulfilled, (state, action) => {
        state.loading = false;
        state.pending = state.pending.filter((r) => r.id !== action.payload.id);
      });
    });
    builder
      .addMatcher(
        (a) => a.type.startsWith('coachRecommendations/') && a.type.endsWith('/pending'),
        (state) => {
          state.loading = true;
          state.error = null;
        }
      )
      .addMatcher(
        (a) => a.type.startsWith('coachRecommendations/') && a.type.endsWith('/rejected'),
        (state, action) => {
          state.loading = false;
          state.error = action.payload || 'Something went wrong';
        }
      );
  },
});

export const { clearRecommendationError } = coachRecommendationSlice.actions;
export default coachRecommendationSlice.reducer;

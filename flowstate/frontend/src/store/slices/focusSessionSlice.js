import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import focusSessionService from '../../services/focusSessionService';
import { getErrorMessage } from '../../services/api';

const makeThunk = (type, call, fallback) =>
  createAsyncThunk(`focusSessions/${type}`, async (arg, { rejectWithValue }) => {
    try {
      const res = await call(arg);
      return res.data;
    } catch (err) {
      return rejectWithValue(getErrorMessage(err, fallback));
    }
  });

export const fetchMySessions = makeThunk(
  'fetchMySessions', (params) => focusSessionService.getMine(params), 'Failed to fetch sessions');
export const fetchAllSessions = makeThunk(
  'fetchAllSessions', (params) => focusSessionService.getAll(params), 'Failed to fetch sessions');
export const createSession = makeThunk(
  'createSession', (data) => focusSessionService.create(data), 'Unable to save FocusSession details');
export const updateSession = makeThunk(
  'updateSession', ({ id, data }) => focusSessionService.update(id, data), 'Unable to save FocusSession details');
export const startSession = makeThunk(
  'startSession', (id) => focusSessionService.start(id), 'Unable to start FocusSession');
export const completeSession = makeThunk(
  'completeSession', (id) => focusSessionService.complete(id), 'Unable to complete FocusSession');
export const abandonSession = makeThunk(
  'abandonSession', ({ id, reason }) => focusSessionService.abandon(id, reason), 'Unable to abandon FocusSession');
export const fetchStats = makeThunk(
  'fetchStats', () => focusSessionService.getStats(), 'Failed to fetch stats');

export const deleteSession = createAsyncThunk('focusSessions/deleteSession', async (id, { rejectWithValue }) => {
  try {
    await focusSessionService.delete(id);
    return id;
  } catch (err) {
    return rejectWithValue(getErrorMessage(err, 'Unable to delete FocusSession'));
  }
});

const upsert = (state, session) => {
  const index = state.items.findIndex((item) => item.id === session.id);
  if (index >= 0) state.items[index] = session;
  else state.items.unshift(session);
};

const initialState = {
  items: [],
  totalPages: 0,
  totalElements: 0,
  currentPage: 0,
  stats: null,
  filterByStatus: '',
  searchQuery: '',
  loading: false,
  error: null,
};

const focusSessionSlice = createSlice({
  name: 'focusSessions',
  initialState,
  reducers: {
    setFilterByStatus: (state, action) => {
      state.filterByStatus = action.payload;
    },
    setSearchQuery: (state, action) => {
      state.searchQuery = action.payload;
    },
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    const onPage = (state, action) => {
      state.loading = false;
      state.items = action.payload.content || [];
      state.totalPages = action.payload.totalPages ?? 0;
      state.totalElements = action.payload.totalElements ?? 0;
      state.currentPage = action.payload.number ?? 0;
    };
    builder
      .addCase(fetchMySessions.fulfilled, onPage)
      .addCase(fetchAllSessions.fulfilled, onPage)
      .addCase(fetchStats.fulfilled, (state, action) => {
        state.loading = false;
        state.stats = action.payload;
      })
      .addCase(deleteSession.fulfilled, (state, action) => {
        state.loading = false;
        state.items = state.items.filter((item) => item.id !== action.payload);
        state.totalElements = Math.max(0, state.totalElements - 1);
      });

    [createSession, startSession, completeSession, updateSession, abandonSession].forEach((thunk) => {
      builder.addCase(thunk.fulfilled, (state, action) => {
        state.loading = false;
        upsert(state, action.payload);
      });
    });

    builder
      .addMatcher(
        (action) => action.type.startsWith('focusSessions/') && action.type.endsWith('/pending'),
        (state) => {
          state.loading = true;
          state.error = null;
        }
      )
      .addMatcher(
        (action) => action.type.startsWith('focusSessions/') && action.type.endsWith('/rejected'),
        (state, action) => {
          state.loading = false;
          state.error = action.payload || action.error?.message || 'Something went wrong';
        }
      );
  },
});

export const { setFilterByStatus, setSearchQuery, clearError } = focusSessionSlice.actions;
export default focusSessionSlice.reducer;

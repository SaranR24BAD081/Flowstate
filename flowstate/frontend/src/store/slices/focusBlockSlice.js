import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import focusBlockService from '../../services/focusBlockService';
import { getErrorMessage } from '../../services/api';

const makeThunk = (type, call, fallback) =>
  createAsyncThunk(`focusBlocks/${type}`, async (arg, { rejectWithValue }) => {
    try {
      const res = await call(arg);
      return res.data;
    } catch (err) {
      return rejectWithValue(getErrorMessage(err, fallback));
    }
  });

export const fetchMyBlocks = makeThunk('fetchMyBlocks', () => focusBlockService.getMine(), 'Failed to fetch focus blocks');
export const createBlock = makeThunk('createBlock', (data) => focusBlockService.create(data), 'Unable to register focus block');
export const protectBlock = makeThunk('protectBlock', (id) => focusBlockService.protect(id), 'Unable to protect block');
export const unprotectBlock = makeThunk('unprotectBlock', (id) => focusBlockService.unprotect(id), 'Unable to remove protection');

export const deleteBlock = createAsyncThunk('focusBlocks/deleteBlock', async (id, { rejectWithValue }) => {
  try {
    await focusBlockService.delete(id);
    return id;
  } catch (err) {
    return rejectWithValue(getErrorMessage(err, 'Unable to delete focus block'));
  }
});

const focusBlockSlice = createSlice({
  name: 'focusBlocks',
  initialState: { items: [], loading: false, error: null },
  reducers: {
    clearBlockError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchMyBlocks.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(createBlock.fulfilled, (state, action) => {
        state.loading = false;
        state.items.push(action.payload);
      })
      .addCase(deleteBlock.fulfilled, (state, action) => {
        state.loading = false;
        state.items = state.items.filter((b) => b.id !== action.payload);
      });
    [protectBlock, unprotectBlock].forEach((thunk) => {
      builder.addCase(thunk.fulfilled, (state, action) => {
        state.loading = false;
        const i = state.items.findIndex((b) => b.id === action.payload.id);
        if (i >= 0) state.items[i] = action.payload;
      });
    });
    builder
      .addMatcher(
        (a) => a.type.startsWith('focusBlocks/') && a.type.endsWith('/pending'),
        (state) => {
          state.loading = true;
          state.error = null;
        }
      )
      .addMatcher(
        (a) => a.type.startsWith('focusBlocks/') && a.type.endsWith('/rejected'),
        (state, action) => {
          state.loading = false;
          state.error = action.payload || 'Something went wrong';
        }
      );
  },
});

export const { clearBlockError } = focusBlockSlice.actions;
export default focusBlockSlice.reducer;

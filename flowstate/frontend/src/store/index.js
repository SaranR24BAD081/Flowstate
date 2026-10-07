import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import focusSessionReducer from './slices/focusSessionSlice';
import sessionGoalReducer from './slices/sessionGoalSlice';
import distractionLogReducer from './slices/distractionLogSlice';
import focusBlockReducer from './slices/focusBlockSlice';
import coachRecommendationReducer from './slices/coachRecommendationSlice';

const store = configureStore({
  reducer: {
    auth: authReducer,
    focusSessions: focusSessionReducer,
    sessionGoals: sessionGoalReducer,
    distractionLogs: distractionLogReducer,
    focusBlocks: focusBlockReducer,
    coachRecommendations: coachRecommendationReducer,
  },
});

export default store;

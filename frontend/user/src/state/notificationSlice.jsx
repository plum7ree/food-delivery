import { createSlice } from '@reduxjs/toolkit';

const notificationSlice = createSlice({
  name: 'notifications',
  initialState: {
    list: [],
    isConnected: false,
  },
  reducers: {
    addNotification: (state, action) => {
      state.list.push(action.payload);
    },
    setConnectionStatus: (state, action) => {
      state.isConnected = action.payload;
    },
  },
});

export const { addNotification, setConnectionStatus } = notificationSlice.actions;
export default notificationSlice.reducer;
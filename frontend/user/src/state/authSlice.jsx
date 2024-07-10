import { createSlice } from '@reduxjs/toolkit';

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    isLoggedIn: false,
    clientId: null,
    credential: null,

  },
  reducers: {
    login: (state, action) => {
      state.isLoggedIn = true;
      state.clientId = action.clientId;
      state.credential = action.credential;
    },
    logout: (state) => {
      state.isLoggedIn = false;
      state.user = null;
    },
  },
});
export const {login, logout} = authSlice.actions;
export const authReducer = authSlice.reducer;
export default authSlice;
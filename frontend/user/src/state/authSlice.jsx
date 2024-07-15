import { createSlice } from '@reduxjs/toolkit';

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    isLoggedIn: false,
    clientId: null,
    credential: null,

  },
  reducers: {
    storeAuth: (state, action) => {
      state.clientId = action.payload.clientId;
      state.credential = action.payload.credential;
    },
    login: (state, action) => {
      state.isLoggedIn = true;
      state.clientId = action.payload.clientId;
      state.credential = action.payload.credential;
    },
    logout: (state) => {
      state.isLoggedIn = false;
      state.user = null;
    },
  },
});
export const {storeAuth, login, logout} = authSlice.actions;
export const authReducer = authSlice.reducer;
export default authSlice;
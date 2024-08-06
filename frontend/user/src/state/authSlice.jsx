import {createSlice} from '@reduxjs/toolkit';

// oauth2 info
// clientId 는 user db 의 clientId 가 아니라 해당 oauth2 authentication server 의 clientId 이다.
const authSlice = createSlice({
   name: 'auth',
   initialState: {
      isLoggedIn: false,
      clientId: null,
      credential: null, //  JWT 토큰

   },
   reducers: {
      storeAuth: (state, action) => {
         state.clientId = action.payload.clientId;
         state.credential = action.payload.credential;
      },
      login: (state, action) => {
         state.isLoggedIn = true;
      },
      logout: (state) => {
         state.isLoggedIn = false;
      },
   },
});
export const {storeAuth, login, logout} = authSlice.actions;
export const authReducer = authSlice.reducer;
export default authSlice;
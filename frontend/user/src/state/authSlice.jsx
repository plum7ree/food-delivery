import {createAsyncThunk, createSlice} from '@reduxjs/toolkit';
import axiosInstance from "./axiosInstance";
import {SERVER_URL} from "./const";

export const asyncGetAuth = createAsyncThunk(
   'auth/getAuth',
   async () => {
      const credential = await sessionStorage.getItem('access-token');
      const userdetails = await sessionStorage.getItem('userdetails');
      const clientId = await sessionStorage.getItem('clientId');
      console.log("getAuth credential: ", credential, " userdetails: ", userdetails)
      return {credential, userdetails, clientId};
   }
);

export const asyncStoreAuth = createAsyncThunk(
   'auth/storeAuth',
   async ({clientId, credential}, thunkAPI) => {
      await sessionStorage.setItem('clientId', clientId);
      await sessionStorage.setItem('access-token', credential);
      return {clientId, credential};
   }
);

// dispatch(asyncLogin(url)) 로 부를 수 있고,
// userdetails 가 리턴되면, extraReducer 쪽에서, addCase 가 fulfilled 인것이 불림.
export const asyncLogin = createAsyncThunk(
   'auth/login',
   async ({userinfoUrl}, thunkAPI) => {
      return axiosInstance.get(userinfoUrl)
         .then((response) => {
            const userdetails = response.data.content;
            console.log("response: ", userdetails);
            sessionStorage.setItem('userdetails', JSON.stringify(userdetails)); // sessionStorage에 JSON 문자열로 저장
            sessionStorage.setItem('pkce_verifier', null);
            return {userdetails}; // userdetails 객체를 반환하여 fulfilled 액션의 payload로 전달
         });
   }
);

export const asyncLogout = createAsyncThunk(
   'auth/logout',
   async (_, thunkAPI) => {
      await sessionStorage.setItem('userdetails', null);
      await sessionStorage.setItem('access-token', null);
      await sessionStorage.setItem('clientId', null);
      await sessionStorage.setItem('pkce_verifier', null);

   }
);

const authSlice = createSlice({
   name: 'auth',
   initialState: {
      credential: null,
      userdetails: null,
      clientId: null,
      getAuthStatus: 'idle',
      storeAuthStatus: 'idle',
      loginStatus: 'idle',
      logoutStatus: 'idle',
   },
   reducers: {},
   extraReducers: (builder) => {
      builder
         .addCase(asyncGetAuth.pending, (state) => {
            state.getAuthStatus = 'pending';
         })
         .addCase(asyncGetAuth.fulfilled, (state, action) => {
            state.getAuthStatus = 'fulfilled';
            state.credential = action.payload.credential;
            state.clientId = action.payload.clientId;
         })
         .addCase(asyncStoreAuth.fulfilled, (state, action) => {
            state.storeAuthStatus = 'fulfilled';
            state.clientId = action.payload.clientId;
            state.credential = action.payload.credential;
         })
         .addCase(asyncLogin.fulfilled, (state, action) => {
            // 위의 asyncLogin 가 userdetails 리턴. action.userdetails 로 받음.
            state.loginStatus = 'fulfilled';
            state.userdetails = action.payload.userdetails;
         })
         .addCase(asyncLogout.fulfilled, (state) => {
            state.logoutStatus = 'fulfilled';
            state.userdetails = null;
         });
   }
});

export default authSlice.reducer;
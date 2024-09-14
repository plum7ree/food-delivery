import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import localStorage from "redux-persist/es/storage";

export const asyncGetAuth = createAsyncThunk(
  'auth/getAuth',
  async () => {
    const credential = await localStorage.getItem('access-token');
    const isLoggedIn = await localStorage.getItem('isLoggedIn');
    const clientId = await localStorage.getItem('clientId');
    console.log("getAuth credential: ", credential, " isLoggedIn: ", isLoggedIn)
    return { credential, isLoggedIn, clientId };
  }
);

export const asyncStoreAuth = createAsyncThunk(
  'auth/storeAuth',
  async ({ clientId, credential }, thunkAPI) => {
    await localStorage.setItem('clientId', clientId);
    await localStorage.setItem('access-token', credential);
    return { clientId, credential };
  }
);

export const asyncLogin = createAsyncThunk(
  'auth/login',
  async (_, thunkAPI) => {
    await localStorage.setItem('isLoggedIn', 'true');
    return true;
  }
);

export const asyncLogout = createAsyncThunk(
  'auth/logout',
  async (_, thunkAPI) => {
    await localStorage.setItem('isLoggedIn', 'false');
    return false;
  }
);

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    credential: null,
    isLoggedIn: false,
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
        state.isLoggedIn = action.payload.isLoggedIn === 'true';
        state.clientId = action.payload.clientId;
      })
      .addCase(asyncStoreAuth.fulfilled, (state, action) => {
        state.storeAuthStatus = 'fulfilled';
        state.clientId = action.payload.clientId;
        state.credential = action.payload.credential;
      })
      .addCase(asyncLogin.fulfilled, (state) => {
        state.loginStatus = 'fulfilled';
        state.isLoggedIn = true;
      })
      .addCase(asyncLogout.fulfilled, (state) => {
        state.logoutStatus = 'fulfilled';
        state.isLoggedIn = false;
      });
  }
});

export default authSlice.reducer;
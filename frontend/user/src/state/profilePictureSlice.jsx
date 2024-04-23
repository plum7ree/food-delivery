import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import axios from "axios";

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8072'
});

export const fetchProfilePicture = createAsyncThunk(
  "profilePicture/fetchProfilePicture",
  async () => {
    const response = await axiosInstance.get("/user/api/profile-picture");
    return response.data;
  }
);

const profilePictureSlice = createSlice({
  name: "profilePicture",
  initialState: {
    url: null,
    status: "idle",
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchProfilePicture.pending, (state) => {
        state.status = "loading";
      })
      .addCase(fetchProfilePicture.fulfilled, (state, action) => {
        state.status = "succeeded";
        state.url = action.payload;
      })
      .addCase(fetchProfilePicture.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.error.message;
      });
  },
});

export default profilePictureSlice.reducer;
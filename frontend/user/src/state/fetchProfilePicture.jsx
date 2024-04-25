import { createAsyncThunk } from "@reduxjs/toolkit";
import axiosInstance from "./axiosInstance";
import { setProfilePictureUrl, setProfilePictureStatus, setProfilePictureError } from "./profilePictureSlice";

export const fetchProfilePicture = createAsyncThunk(
   // typePrefix??
  "profilePicture/fetchProfilePicture",
  async (_, { dispatch }) => {
    try {
      dispatch(setProfilePictureStatus("loading"));
      const response = await axiosInstance.get("/user/api/profile-picture");
      dispatch(setProfilePictureUrl(response.data));
      dispatch(setProfilePictureStatus("succeeded"));
    } catch (error) {
      dispatch(setProfilePictureError(error.message));
      dispatch(setProfilePictureStatus("failed"));
    }
  }
);

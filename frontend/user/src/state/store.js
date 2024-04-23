import { configureStore } from "@reduxjs/toolkit";
import profilePictureReducer from "./profilePictureSlice";
import axiosReducer from "./axiosSlice";

export default configureStore({
  reducer: {
    profilePicture: profilePictureReducer,
    axiosInstance: axiosReducer,
  },
});
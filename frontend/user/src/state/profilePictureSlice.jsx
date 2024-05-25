import {createSlice} from "@reduxjs/toolkit";

const profilePictureSlice = createSlice({
   name: "profilePicture",
   initialState: {
      url: null,
      status: "idle",
      error: null,
   },
   reducers: {
      setProfilePictureUrl: (state, action) => {
         state.url = action.payload;
      },
      setProfilePictureStatus: (state, action) => {
         state.status = action.payload;
      },
      setProfilePictureError: (state, action) => {
         state.error = action.payload;
      },
   },
});

export const {setProfilePictureUrl, setProfilePictureStatus, setProfilePictureError} = profilePictureSlice.actions;

export default profilePictureSlice.reducer;

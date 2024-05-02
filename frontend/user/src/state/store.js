import {configureStore} from "@reduxjs/toolkit";
import profilePictureReducer from "./profilePictureSlice";

export default configureStore({
   reducer: {
      profilePicture: profilePictureReducer,
   },
});

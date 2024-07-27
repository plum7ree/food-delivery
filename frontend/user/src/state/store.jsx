import {combineSlices, configureStore} from '@reduxjs/toolkit';
import profilePictureSlice, {profilePictureReducer} from "./profilePictureSlice";
import selectedMenuSlice, {selectedMenuReducer} from "./checkout/selectedMenuSlice";
import authSlice, {authReducer} from "./authSlice";
import notificationReducer from './notificationSlice';
import websocketMiddleware from './websocketMiddleware';


export default configureStore({
   reducer: {
      profilePicture: profilePictureReducer,
      selectedMenu: selectedMenuReducer,
      auth: authReducer,
      notifications: notificationReducer,
   },

   middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware().concat(websocketMiddleware),
});

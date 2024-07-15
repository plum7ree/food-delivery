import { combineSlices, configureStore } from '@reduxjs/toolkit';
import profilePictureSlice, {profilePictureReducer} from "./profilePictureSlice";
import selectedMenuSlice, {selectedMenuReducer} from "./checkout/selectedMenuSlice";
import authSlice, {authReducer} from "./authSlice";


export default configureStore({
   reducer: {
      profilePicture: profilePictureReducer,
      selectedMenu: selectedMenuReducer,
      auth: authReducer
   },
});

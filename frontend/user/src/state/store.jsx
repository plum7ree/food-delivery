import {configureStore} from "@reduxjs/toolkit";
import profilePictureReducer from "./profilePictureSlice";
import selectedMenuReducer from "./checkout/selectedMenuSlice";

export default configureStore({
   reducer: {
      profilePicture: profilePictureReducer,
      selectedMenu: selectedMenuReducer
   },
});

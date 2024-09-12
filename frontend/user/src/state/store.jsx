import {combineReducers, combineSlices, configureStore} from '@reduxjs/toolkit';
import profilePictureReducer from "./profilePictureSlice";
import selectedMenuReducer from "./checkout/selectedMenuSlice";
import authReducer from "./authSlice";
import notificationReducer from './notificationSlice';
import websocketMiddleware from './websocketMiddleware';
import restaurantReducer from './restaurantSlice';
import driverReducer from './driverSlice';
import {persistStore, persistReducer} from 'redux-persist'
import storage from 'redux-persist/lib/storage'


export default configureStore({
   reducer: {
      profilePicture: profilePictureReducer,
      selectedMenu: selectedMenuReducer,
      auth: authReducer,
      notifications: notificationReducer,
      restaurants: restaurantReducer,
      drivers: driverReducer,

   },

   middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware().concat(websocketMiddleware),
});


// 루트 리듀서 정의
// const rootReducer = combineReducers({
//    profilePicture: profilePictureReducer,
//    selectedMenu: selectedMenuReducer,
//    auth: authReducer,
//    notifications: notificationReducer,
// });
//
// // Persist 설정
// const persistConfig = {
//    key: 'root',
//    storage,
// }
// // Persisted 리듀서 생성
// const persistedReducer = persistReducer(persistConfig, rootReducer);
//
// // 스토어 생성
// export const store = configureStore({
//    reducer: persistedReducer,
//    middleware: (getDefaultMiddleware) =>
//       getDefaultMiddleware({
//          serializableCheck: {
//             ignoredActions: ['persist/PERSIST', 'persist/REHYDRATE'], // 필요한 경우 추가
//          },
//       }).concat(websocketMiddleware),
// });
//
// // Persistor 생성
// export const persistor = persistStore(store);


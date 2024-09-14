// import { createSlice } from '@reduxjs/toolkit';
//
// const userSlice = createSlice({
//   name: 'user',
//   initialState: {
//     id: null,
//     profilePictureUrl: null,
//     name: null,
//     email: null,
//     // 필요한 다른 사용자 정보 필드들...
//   },
//   reducers: {
//     setUserInfo: (state, action) => {
//       return { ...state, ...action.payload };
//     },
//     clearUserInfo: (state) => {
//       return {
//         id: null,
//         profilePictureUrl: null,
//         name: null,
//         email: null,
//         // 다른 필드들도 초기화...
//       };
//     },
//     updateProfilePicture: (state, action) => {
//       state.profilePictureUrl = action.payload;
//     },
//     // 필요한 다른 리듀서들...
//   },
// });
//
// export const { setUserInfo, clearUserInfo, updateProfilePicture } = userSlice.actions;
// export const userReducer = userSlice.reducer;
// export default userSlice;
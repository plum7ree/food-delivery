// src/state/restaurantSlice.js
import {createSlice, createAsyncThunk} from '@reduxjs/toolkit';
import axiosInstance from './axiosInstance';

// Async thunk to fetch restaurants
export const fetchRestaurantsByType = createAsyncThunk(
   'restaurants/fetchByType',
   async ({type, limit}) => {
      const response = await axiosInstance.get(`/user/api/restaurant/restaurants?type=${type}&limit=${limit}`);
      /**
       * response.data: page 구조체
       * {
       *   "content": [
       *     { "id": "1", "name": "Restaurant 1", "type": "BURGER" },
       *     { "id": "2", "name": "Restaurant 2", "type": "BURGER" },
       *     { "id": "3", "name": "Restaurant 3", "type": "BURGER" },
       *     { "id": "4", "name": "Restaurant 4", "type": "BURGER" },
       *     { "id": "5", "name": "Restaurant 5", "type": "BURGER" },
       *     { "id": "6", "name": "Restaurant 6", "type": "BURGER" },
       *     { "id": "7", "name": "Restaurant 7", "type": "BURGER" },
       *     { "id": "8", "name": "Restaurant 8", "type": "BURGER" },
       *     { "id": "9", "name": "Restaurant 9", "type": "BURGER" },
       *     { "id": "10", "name": "Restaurant 10", "type": "BURGER" }
       *   ],
       *   "empty": false,
       *   "first": true,
       *   "last": true,
       *   "number": 0,
       *   "numberOfElements": 10,
       *   "pageable": {
       *     "sort": {
       *       "empty": true,
       *       "sorted": false,
       *       "unsorted": true
       *     },
       *     "offset": 0,
       *     "pageNumber": 0,
       *     "pageSize": 10,
       *     "paged": true,
       *     "unpaged": false
       *   },
       *   "size": 10,
       *   "sort": {
       *     "empty": true,
       *     "sorted": false,
       *     "unsorted": true
       *   },
       *   "totalElements": 10,
       *   "totalPages": 1
       * }
       */
      return {type: type, data: response.data.content};
   }
);

const restaurantSlice = createSlice({
   name: 'restaurants',
   initialState: {
      currentRestaurant: {},

      restaurantListByType: {
         /**
          * restaurantListByType: {
          *    "BURGER" : [],
          *    "PIZZA": [],
          * }
          */
      }
   },
   reducers: {},
   extraReducers: (builder) => {
      builder
         .addCase(fetchRestaurantsByType.fulfilled, (state, action) => {
            const {type, data} = action.payload;
            state.restaurantListByType[type] = data;
         });
   }
});

export default restaurantSlice.reducer;



// src/slices/driverSlice.js
import { createSlice } from '@reduxjs/toolkit';

const driverSlice = createSlice({
  name: 'drivers',
  initialState: {
    drivers: {}, // 드라이버 정보를 저장할 Object (Map의 변형)
  },
  reducers: {
    addDriver(state, action) {
      const { driverId, lat, lon } = action.payload;
      state.drivers[driverId] = { driverId, lat, lon };
    },
    removeDriver(state, action) {
      const { driverId } = action.payload;
      delete state.drivers[driverId];
    },
    clearDrivers(state) {
      state.drivers = {};
    },
  },
  selectors: {
    getDriversAsArray(state) {
      return Object.values(state.drivers);
    },
  },
});

export const { addDriver, removeDriver, clearDrivers } = driverSlice.actions;
export const { getDriversAsArray } = driverSlice.selectors;

export default driverSlice.reducer;

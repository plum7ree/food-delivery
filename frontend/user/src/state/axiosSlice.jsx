import { createSlice } from "@reduxjs/toolkit";
import axios from "axios";

const axiosSlice = createSlice({
  name: "axios",
  initialState: {
    instance: axios.create({
      baseURL: "http://localhost:8072",
    }),
  },
  reducers: {},
});

export default axiosSlice.reducer;
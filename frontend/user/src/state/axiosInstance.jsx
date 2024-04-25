import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8072",
});

export default axiosInstance;

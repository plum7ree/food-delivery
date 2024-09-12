import axios from "axios";
import {SERVER_URL} from "./const";

const axiosInstance = axios.create({
   baseURL: SERVER_URL,
});
// Request interceptor to add Bearer token
axiosInstance.interceptors.request.use(
   (config) => {
      // Retrieve the token from localStorage or any other method
      const token = localStorage.getItem('access-token'); // Adjust this as needed

      // Add the Bearer token to the Authorization header if it exists
      if (token) {
         config.headers.Authorization = `Bearer ${token}`;
      }

      return config;
   },
   (error) => {
      // Handle request error
      return Promise.reject(error);
   }
);
export default axiosInstance;

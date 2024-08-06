import axios from "axios";
import {SERVER_URL} from "./const";
import store from './store'; // Redux 스토어 import

const axiosInstance = axios.create({
   baseURL: SERVER_URL,
});

export default axiosInstance;

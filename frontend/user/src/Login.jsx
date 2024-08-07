import React from "react";
import {GoogleOAuthProvider, GoogleLogin} from "@react-oauth/google";
import axios from "axios";
import {login, storeAuth} from './state/authSlice';
import {useDispatch} from 'react-redux';
import {useNavigate} from "react-router-dom";
import {SERVER_URL} from "./state/const";
import axiosInstance from "./state/axiosInstance";

const Login = () => {
   const dispatch = useDispatch();
   const navigate = useNavigate();
   const handleLoginSuccess = async (response) => {
      try {
         console.log("oauth2 login successful")
         console.log(response)
         dispatch(storeAuth({clientId: response.clientId, credential: response.credential}));
         const result = await axiosInstance.get(SERVER_URL + "/user/api/info", {
            headers: {
               Authorization: `Bearer ${response.credential}`,
            },
         });
         // 에러 발생 없으면 로그인 처리 후 홈으로 리다이렉트
         dispatch(login({clientId: response.clientId, credential: response.credential}));
         navigate('/');

      } catch (error) {
         console.error("After Oauth2 request to user service failed. 가입이 필요해서, registration 페이지로 전환.:", error);
         if (error.code === "ERR_NETWORK") {
            // id가 빈 문자열이면 /register로 리다이렉트
            navigate('/register');
         } else if (error.response.code === 503) {
            console.error("user service not available");
         }
      }
   };

   return (
      <GoogleOAuthProvider clientId="790229558589-2rc9t0esnb5p8meh64jsof6a9p7vf1qe.apps.googleusercontent.com">
         <div>
            <h1>Google OAuth2 Login with React</h1>
            <GoogleLogin
               onSuccess={handleLoginSuccess}
               onError={() => console.log("Login Failed")}
            />
         </div>
      </GoogleOAuthProvider>
   );
};

export default Login;
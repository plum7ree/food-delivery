import React from "react";
import {GoogleOAuthProvider, GoogleLogin} from "@react-oauth/google";
import axios from "axios";
import {login} from './state/authSlice';
import {useDispatch} from 'react-redux';
import {useNavigate} from "react-router-dom";

const Login = () => {
   const dispatch = useDispatch();
   const navigate = useNavigate();
   const handleLoginSuccess = async (response) => {
      try {
         console.log("login successful")
         console.log(response)
         const result = await axios.get("http://localhost:8080/user", {
            headers: {
               Authorization: `Bearer ${response.credential}`,
            },
         });
         dispatch(login(response));
         navigate('/');
      } catch (error) {
         console.error("Login failed:", error);
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
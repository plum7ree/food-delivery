import React, {useEffect, useState} from "react";
import {GoogleOAuthProvider, GoogleLogin} from "@react-oauth/google";
import axios from "axios";
import {asyncLogin, asyncLogout, asyncStoreAuth} from '../state/authSlice';
import {useDispatch} from 'react-redux';
import {Link, useNavigate} from "react-router-dom";
import {SERVER_URL} from "../state/const";
import axiosInstance from "../state/axiosInstance";
import {Box, Button, Checkbox, FormControlLabel, Grid, TextField, Typography} from "@mui/material";
import {Container, Stack} from "@mui/system";
import GoogleIcon from '@mui/icons-material/Google';
import {UserManager, WebStorageStateStore} from "oidc-client-ts";

/**
 * TODO @react-oauth/google 라이브러리는 implicit, auth code 둘다 지원은 하지만 pkce 는 지원안하는듯.
 * @returns {Element}
 * @constructor
 */
const oidcConfig = {
   authority: 'http://localhost:9000/oauth2/authorize', // 인증 서버 URL
   client_id: 'webappfooddeliverypublicclient',          // 클라이언트 ID
   redirect_uri: 'http://localhost:5173', // 인증 후 돌아올 URL
   response_type: 'code',                // authorization code 방식
   scope: 'openid profile email',        // 요청할 scope
   loadUserInfo: true,                   // 인증 후 사용자 정보 요청
   userStore: new WebStorageStateStore({store: window.sessionStorage}),
};

const Login = () => {
   const [user, setUser] = useState(null)
   const userManager = new UserManager(oidcConfig);

   useEffect(() => {
      if (window.location.pathname === '/callback') {
         handleCallback();
      } else {
         userManager.getUser().then(setUser);
      }
   }, []);

   const handleCallback = async () => {
      try {
         const authenticatedUser = await userManager.signinRedirectCallback();
         setUser(authenticatedUser);
      } catch (error) {
         console.error('로그인 콜백 처리 에러:', error);
      }
   };
   const handleLogin = () => {
      userManager.signinRedirect().then(r => console.log(r));
   };

   const handleLogout = () => {
      userManager.signoutRedirect();
      setUser(null);
   };
   // const dispatch = useDispatch();
   // const navigate = useNavigate();
   // const handleLoginSuccess = async (response) => {
   //    try {
   //       await dispatch(asyncStoreAuth({clientId: response.clientId, credential: response.credential}));
   //       const result = await axiosInstance.get(SERVER_URL + "/user/api/info");
   //       // 에러 발생 없으면 로그인 처리 후 홈으로 리다이렉트
   //       await dispatch(asyncLogin());
   //       navigate('/');
   //
   //    } catch (error) {
   //       console.log("user api info error: ", error);
   //       if (error.code === "ERR_BAD_REQUEST") {
   //          // id가 빈 문자열이면 /register로 리다이렉트
   //          console.error("After Oauth2 request to user service failed. 가입이 필요해서, registration 페이지로 전환.:", error);
   //          await dispatch(asyncLogout());
   //          navigate('/register');
   //       } else if (error.response.code === 503) {
   //          console.error("user service not available");
   //          await dispatch(asyncLogout());
   //       } else {
   //          console.error("예측못한 에러. 핸들링 불가");
   //       }
   //    }
   // };

   return (
      <Box
         display="flex"
         alignItems="center"
         justifyContent="center"
         minHeight="100vh"
         bgcolor="#f5f5f5"
      >
         <Box display="flex" flexDirection="column" alignItems="left"
              sx={{mt: -30, maxWidth: 400, width: '100%', p: 4, bgcolor: 'white', borderRadius: 2, boxShadow: 3}}>
            <Typography variant="h6" color="textSecondary">Please enter your details</Typography>

            <Typography variant="h4" fontWeight="bold" gutterBottom sx={{mt: 1, mb: 3}}>Welcome back</Typography>
            <Box component="form" width="100%">
               <TextField
                  fullWidth
                  label="Email address"
                  margin="normal"
                  type="email"
                  required
               />
               <TextField
                  fullWidth
                  label="Password"
                  margin="normal"
                  type="password"
                  required
               />
               {/*<Stack direction="row" justifyContent="space-between" alignItems="center" sx={{mb: 2}}>*/}
               {/*   <FormControlLabel control={<Checkbox/>} label="Remember for 30 days"/>*/}
               {/*   <Link href="#" color="primary">Forgot password</Link>*/}
               {/*</Stack>*/}
               <Button fullWidth
                       onClick={handleLogin}
                       variant="contained" color="primary" sx={{mt: 5, mb: 2}}>
                  Sign in
               </Button>
               <Button
                  fullWidth
                  variant="outlined"
                  startIcon={<GoogleIcon/>}
               >
                  Sign in with Google
               </Button>
               <Typography align="center" sx={{mt: 2}}>
                  Don't have an account? <Link href="#">Sign up</Link>
               </Typography>
            </Box>
         </Box>
      </Box>
   );
};

export default Login;
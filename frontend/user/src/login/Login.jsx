import React, {useEffect, useState} from 'react';
import pkceChallenge from 'pkce-challenge';
import {Box, Button, Paper, TextField, Typography} from "@mui/material";
import {Container} from "@mui/system";
import GoogleIcon from "@mui/icons-material/Google";
import {Link, useNavigate} from "react-router-dom";
import {asyncLogin, asyncLogout, asyncStoreAuth} from "../state/authSlice";
import axiosInstance from "../state/axiosInstance";
import {SERVER_URL} from "../state/const";
import {useDispatch} from 'react-redux';

const clientId = 'webappfooddeliverypublicclient';
const authorizationEndpoint = 'http://localhost:9000/oauth2/authorize';
const tokenEndpoint = 'http://localhost:9000/oauth2/token';
const redirectUri = 'http://localhost:5173/login';

const Login = () => {
   const [authCode, setAuthCode] = useState(null);
   const [accessToken, setAccessToken] = useState(null);

   // 1. request code
   const login = async () => {
      const {code_challenge, code_verifier} = (await pkceChallenge());
      console.info('get code code_verifier: ', code_verifier);
      sessionStorage.setItem('pkce_verifier', code_verifier);

      const authUrl = `${authorizationEndpoint}?response_type=code&client_id=${clientId}&redirect_uri=${encodeURIComponent(
         redirectUri
      )}&scope=openid&code_challenge=${code_challenge}&code_challenge_method=S256`;

      window.location.href = authUrl;
   };
   // 2. store code
   useEffect(() => {
      const urlParams = new URLSearchParams(window.location.search);
      const code = urlParams.get('code');
      if (code) {
         setAuthCode(code);
      }
   }, []);

   // 3-1. exchange token
   const exchangeToken = async () => {
      console.info('Exchange token');
      const code_verifier = sessionStorage.getItem('pkce_verifier');
      console.log("authCode: ", authCode, "codeverifier: ", code_verifier);
      const response = await fetch(tokenEndpoint, {
         method: 'POST',
         headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
         },
         body: new URLSearchParams({
            client_id: clientId,
            grant_type: 'authorization_code',
            code: authCode,
            redirect_uri: redirectUri,
            code_verifier,
         }),
      });

      const data = await response.json();
      setAccessToken(data.access_token);
   };
   // 3. exchange token
   useEffect(() => {
      if (authCode && !accessToken) {
         exchangeToken();
      }
   }, [authCode, accessToken]);

   // 4. store access token.
   useEffect(() => {
      if (authCode && accessToken) {
         handleLoginSuccess(accessToken);
      }
   }, [authCode, accessToken]);

   // 4-1. store access token.
   const dispatch = useDispatch();
   const navigate = useNavigate();
   const handleLoginSuccess = async (accessToken) => {

      await dispatch(asyncStoreAuth({clientId: clientId, credential: accessToken}));
      // 에러 발생 없으면 로그인 처리 후 홈으로 리다이렉트
      // TODO userinfo 정보를 저장하는 것으로 로 바꿔야함.
      await dispatch(asyncLogin({userinfoUrl: SERVER_URL + "/user/account/info"}));
      navigate('/eats');


   };
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

               {/*<Stack direction="row" justifyContent="space-between" alignItems="center" sx={{mb: 2}}>*/}
               {/*   <FormControlLabel control={<Checkbox/>} label="Remember for 30 days"/>*/}
               {/*   <Link href="#" color="primary">Forgot password</Link>*/}
               {/*</Stack>*/}
               <Button fullWidth
                       onClick={login}
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
}

export default Login;

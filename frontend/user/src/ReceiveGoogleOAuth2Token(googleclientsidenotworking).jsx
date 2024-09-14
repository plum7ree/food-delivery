// import React, {useEffect} from "react";
// import {useLocation, useNavigate} from "react-router-dom";
// import {cookieStorage} from "./state/security/storage";
// import {login, storeAuth} from "./state/authSlice";
// import axiosInstance from "./state/axiosInstance";
// import {SERVER_URL} from "./state/const";
// import {useDispatch} from "react-redux";
//
// /**
//  * https://developers.google.com/identity/protocols/oauth2/javascript-implicit-flow?hl=ko#oauth-2.0-endpoints_3
//  *
//  */
// const ReceiveGoogleOAuth2TokenGoogleclientsidenotworking = () => {
//
//    const navigate = useNavigate()
//    const dispatch = useDispatch()
//
//    /**
//     * 에러 access token 이 이렇게 생겼다. 문제 해결해보자.
//     * ya29.a0AcM612y8U9q_s8DBS6eIRd4nPAP-NgkKrFwARPKEQ3ZoyJ5_x_OroI_W1z57rv4FbvVCTPSccaSN9TccmIpE7Kk2bixqutJR6OEOEia3EIo8hkncgt3C8SCzEBklGWt9n5Ry-QJnmvuAVz38P8u9Pyi7j4Bt3Y5zbwaCgYKAeYSARMSFQHGX2MiZhDVeQ1l7a6-4d_m-ke_FA0169
//     * https://github.com/parse-community/parse-server/issues/6849
//     *
//     *
//     */
//
//    useEffect(() => {
//       const getAccessToken = () => {
//          // Parse query string to see if page request is coming from OAuth 2.0 server.
//          var fragmentString = location.hash.substring(1);
//          var params = {};
//          var regex = /([^&=]+)=([^&]*)/g, m;
//          while (m = regex.exec(fragmentString)) {
//             params[decodeURIComponent(m[1])] = decodeURIComponent(m[2]);
//          }
//          if (Object.keys(params).length > 0 && params['state']) {
//             if (params['state'] == localStorage.getItem('state')) {
//                // param 생김새
//                // access_token:""
//                // authuser:"0"
//                // expires_in:"3599"
//                // prompt:"consent"
//                // scope:"email profile https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/drive.metadata.readonly https://www.googleapis.com/auth/userinfo.profile openid"
//                // state:"65-n46iC"
//                // token_type:"Bearer"
//                cookieStorage.setItem('access_token', params['access_token']);
//                cookieStorage.setItem('access_token_expires_in', params['expires_in']);
//                cookieStorage.setItem('token_type', params['token_type']);
//             } else {
//                console.log('State mismatch. Possible CSRF attack');
//             }
//          }
//       }
//       const handleLoginSuccess = async () => {
//          try {
//
//             const accessToken = cookieStorage.getItem('access_token')
//             console.log(accessToken)
//             const result = await axiosInstance.get(SERVER_URL + "/user/api/info", {
//                headers: {
//                   Authorization: `Bearer ${cookieStorage.getItem('access_token')}`,
//                },
//             });
//             // 에러 발생 없으면 로그인 처리 후 홈으로 리다이렉트
//             dispatch(login());
//             navigate('/');
//
//          } catch (error) {
//             if (error.code === "ERR_NETWORK") {
//                console.error("After Oauth2 request to user service failed. 가입이 필요해서, registration 페이지로 전환.:", error);
//                // id가 빈 문자열이면 /register로 리다이렉트
//                navigate('/register');
//             } else if (error.code === "ERR_BAD_REQUEST") {
//                console.error("/user/api/info ERR_BAD_REQUEST")
//             }
//             else if (error.response.code === 503) {
//                console.error("user service not available");
//             }
//          }
//       };
//       getAccessToken()
//       handleLoginSuccess()
//
//    }, [])
//
//
//    return (
//       <div>
//       </div>
//    );
// };
//
// export default ReceiveGoogleOAuth2TokenGoogleclientsidenotworking;
import React, {useEffect} from "react";
import {GoogleOAuthProvider, GoogleLogin} from "@react-oauth/google";
import axios from "axios";
import {login, savePkce, storeAuth} from './state/authSlice';
import {useDispatch, useSelector} from 'react-redux';
import {useLocation, useNavigate} from "react-router-dom";
import {FRONTEND_HOME_URL, GOOGLE_CLIENT_ID, GOOGLE_OAUTH2_AUTHORIZE_ENDPOINT_URL, SERVER_URL} from "./state/const";
import axiosInstance from "./state/axiosInstance";
import pkceChallenge from 'pkce-challenge'

/**
 * https://developers.google.com/identity/protocols/oauth2/javascript-implicit-flow?hl=ko#oauth-2.0-endpoints_3
 *
 * response_type : token 으로 요청하고
 * csrf 공격 방지용 random 한 state localStorage 에 저장한 후 보내고 리다이렉트 되었을때 url param 에서 추출한 후 비교한다.
 *
 * @returns {Element}
 * @constructor
 */
const Login = () => {

   const navigate = useNavigate()
   const dispatch = useDispatch()
   useEffect(() => {
         function oauth2SignIn() {
            function generateCryptoRandomState() {
               const randomValues = new Uint32Array(2);
               window.crypto.getRandomValues(randomValues);

               // Encode as UTF-8
               const utf8Encoder = new TextEncoder();
               const utf8Array = utf8Encoder.encode(
                  String.fromCharCode.apply(null, randomValues)
               );

               // Base64 encode the UTF-8 data
               return btoa(String.fromCharCode.apply(null, utf8Array))
                  .replace(/\+/g, '-')
                  .replace(/\//g, '_')
                  .replace(/=+$/, '');
            }

            // create random state value and store in local storage
            var state = generateCryptoRandomState();
            localStorage.setItem('state', state);

            // Google's OAuth 2.0 endpoint for requesting an access token
            var oauth2Endpoint = 'https://accounts.google.com/o/oauth2/v2/auth';

            // Create element to open OAuth 2.0 endpoint in new window.
            var form = document.createElement('form');
            form.setAttribute('method', 'GET'); // Send as a GET request.
            form.setAttribute('action', oauth2Endpoint);

            // Parameters to pass to OAuth 2.0 endpoint.
            var params = {
               'client_id': GOOGLE_CLIENT_ID,
               'redirect_uri': FRONTEND_HOME_URL + "/token",
               'scope': 'https://www.googleapis.com/auth/drive.metadata.readonly',
               'state': state,
               'include_granted_scopes': 'true',
               'response_type': 'token',
            };

            // Add form parameters as hidden input values.
            for (var p in params) {
               var input = document.createElement('input');
               input.setAttribute('type', 'hidden');
               input.setAttribute('name', p);
               input.setAttribute('value', params[p]);
               form.appendChild(input);
            }

            // Add form to page and submit it to open the OAuth 2.0 endpoint.
            document.body.appendChild(form);
            form.submit();
         }

         oauth2SignIn()

      }, []
   )


   return (
      <div>
      </div>
   );
};

export default Login;
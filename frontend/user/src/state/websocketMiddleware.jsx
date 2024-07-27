
// socketjs error: global is not defined
// import from a built version: 'sockjs-client/dist/sockjs' instead of 'sockjs-client'
// https://github.com/sockjs/sockjs-client/issues/439
import SockJS from "sockjs-client/dist/sockjs"
import {Client} from '@stomp/stompjs';
import {addNotification, setConnectionStatus} from './notificationSlice';

const websocketMiddleware = store => {
   let client = null;

   const onConnect = () => {
      console.info('websocket connected successfully.');
      store.dispatch(setConnectionStatus(true));
      client.subscribe('/user/queue/notifications', message => {
         const newNotification = JSON.parse(message.body);
         store.dispatch(addNotification(newNotification));
      });
   };

   const onDisconnect = () => {
      store.dispatch(setConnectionStatus(false));
   };

   // const headers = {
   //    'Authorization': 'Bearer ' + jwtToken // OAuth2 로그인 후 받은 JWT 토큰
   // };


   return next => action => {
      switch (action.type) {
         case 'notifications/connect':
            if (client) client.deactivate();

            client = new Client({
               webSocketFactory: () => new SockJS('http://localhost:8074/sockjs'),
               onConnect,
               onDisconnect,
            });

            client.activate();
            break;

         case 'notifications/disconnect':
            if (client) {
               client.deactivate();
               client = null;
            }
            break;

         default:
            return next(action);
      }
   };
};

export default websocketMiddleware;
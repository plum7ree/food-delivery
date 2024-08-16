// socketjs error: global is not defined
// import from a built version: 'sockjs-client/dist/sockjs' instead of 'sockjs-client'
// https://github.com/sockjs/sockjs-client/issues/439
import SockJS from "sockjs-client/dist/sockjs"
import {Client, Stomp} from '@stomp/stompjs';
import {addNotification, setConnectionStatus} from './notificationSlice';

// 로그인 후 jwt 담아서 ws 연결
const websocketMiddleware = store => {
   let client = null;

   const onConnect = () => {
      console.info('websocket connected successfully.');
      store.dispatch(setConnectionStatus(true));
      client.subscribe('/user/queue/notifications', message => {
         const newNotification = JSON.parse(message.body);
         console.info("got message {}", newNotification);
         store.dispatch(addNotification(newNotification));
      });
      client.subscribe('/user/topic/heartbeat', message => {
         const newNotification = JSON.parse(message.body);
         console.info("got heartbeat {}", newNotification);
         store.dispatch(addNotification(newNotification));
      });
   };

   const onDisconnect = () => {
      store.dispatch(setConnectionStatus(false));
   };


   return next => action => {
      switch (action.type) {
         case 'notifications/connect':
            if (client) client.deactivate();
            const state = store.getState();
            const credential = state.auth.credential;
            if (!credential) {
               console.error('No JWT token available. WebSocket connection aborted.');
               // next는 다음 middleware 에게 action 전달
               return next(action);
            }
            console.info('JWT token exists trying to connect');

            // https://tjdans.tistory.com/25
            // https://ably.com/blog/websocket-authentication
            // sockjs 는 http 로 handshake 한 다음 ws 로 승격됨.
            const socket = new SockJS('http://localhost:8080/sockjs');
            const stompClient = Stomp.over(socket);
            let headers = {Authorization: `Bearer ${credential}`};
            stompClient.connect(headers, (frame) => {
               onConnect();
            }, (error) => {
               onDisconnect();
            })
            break;

         case 'notifications/disconnect':
            if (client) {
               onDisconnect();
               client = null;
            }
            break;

         default:
            return next(action);
      }
   };
};

export default websocketMiddleware;
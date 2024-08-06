// socketjs error: global is not defined
// import from a built version: 'sockjs-client/dist/sockjs' instead of 'sockjs-client'
// https://github.com/sockjs/sockjs-client/issues/439
import SockJS from "sockjs-client/dist/sockjs"
import {Client} from '@stomp/stompjs';
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


            // client = new Client({
            //    webSocketFactory: () => new SockJS(`http://localhost:8080/sockjs?token=${credential}`,
            //       null,
            //       {withCredentials: true}),
            //    onConnect,
            //    onDisconnect,
            // });

            // client.activate();
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
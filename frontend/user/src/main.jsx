import React from 'react';
import ReactDOM from 'react-dom/client';
import {Provider} from 'react-redux';
import {store, persistor} from './state/store';
// import store from './state/store'
import App from './App';
import {PersistGate} from "redux-persist/integration/react";

const rootElement = document.getElementById('root');
const root = ReactDOM.createRoot(rootElement);

root.render(
   <Provider store={store}>
      <PersistGate loading={null} persistor={persistor}>
         <App/>
      </PersistGate>
   </Provider>
);
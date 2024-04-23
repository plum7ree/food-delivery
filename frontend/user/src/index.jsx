import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { Provider } from "react-redux";
import store from "./state/store";

import App from "./App";
// import Login from "./Login";
// import TossPay from "./TossPay";
import UberLikeApp from "./UberLikeApp";
import EatsMain from "./eats/EatsMain";

const rootElement = document.getElementById("root");
const root = createRoot(rootElement);

root.render(
  <StrictMode>
  <Provider store={store}>
    <App />
  </Provider>,
  </StrictMode>,
);

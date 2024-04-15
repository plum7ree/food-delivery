import { StrictMode } from "react";
import { createRoot } from "react-dom/client";

import App from "./App";
// import Login from "./Login";
// import TossPay from "./TossPay";
import UberLikeApp from "./UberLikeApp";
import EatsMain from "./EatsMain";

const rootElement = document.getElementById("root");
const root = createRoot(rootElement);

root.render(
  <StrictMode>
    <App />
  </StrictMode>,
);

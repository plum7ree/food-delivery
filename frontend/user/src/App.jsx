import React, { useState } from "react";
import "./styles.css";
import UberLikeApp from "./UberLikeApp";
import EatsMain from "./EatsMain";

export default function App() {
  const [appMode, setAppMode] = useState(null);

  const handleUberClick = () => {
    setAppMode("uber");
  };

  const handleUberEatsClick = () => {
    setAppMode("uberEats");
  };

  return (
    <div className="App">
      {!appMode && (
        <div className="mode-selection">
          <h1>선택하세요</h1>
          <div className="buttons">
            <button onClick={handleUberClick}>Uber</button>
            <button onClick={handleUberEatsClick}>Uber Eats</button>
          </div>
        </div>
      )}
      {appMode === "uber" && <UberLikeApp />}
      {appMode === "uberEats" && <EatsMain />}
    </div>
  );
}
import React, { useState } from "react";
import { BrowserRouter as Router, Link, Route, Routes } from "react-router-dom";
import "./styles.css";
import UberLikeApp from "./UberLikeApp";
import EatsMain from "./eats/EatsMain";
import MyPage from "./eats/MyPage";
import RestaurantRegistration from "./eats/RestaurantRegistration";
import RestaurantManage from "./eats/RestaurantManage";
import { AppBar, Tabs, Tab } from "@mui/material";
import { styled } from "@mui/material/styles";

const StyledTab = styled(Tab)({
  color: "#fff",
});

export default function App() {
  const [selectedTab, setSelectedTab] = useState(0);

  const handleTabChange = (event, newValue) => {
    setSelectedTab(newValue);
  };

  return (
    <div className="App">
      <Router>
        <AppBar position="static">
          <Tabs value={selectedTab} onChange={handleTabChange}>
            <StyledTab label="홈" component={Link} to="/" />
            <StyledTab label="Uber" component={Link} to="/uber" />
            <StyledTab label="Uber Eats" component={Link} to="/eats" />
          </Tabs>
        </AppBar>
        <Routes>
          <Route path="/uber" element={<UberLikeApp />} />
          <Route path="/eats" element={<EatsMain />} />
          <Route path="/eats/mypage" element={<MyPage />} />
          <Route path="/eats/restaurant-registration" element={<RestaurantRegistration />} />
          <Route path="/eats/restaurant-manage" element={<RestaurantManage />} />
        </Routes>
      </Router>
    </div>
  );
}
import React, {useEffect, useState} from "react";
import {createHashRouter, createBrowserRouter, Link, RouterProvider, Navigate} from "react-router-dom";
import "./styles.css";
import UberLikeApp from "./UberLikeApp";
import EatsMain from "./eats/EatsMain";
import {Box, Grid, Typography} from "@mui/material";
import {styled} from "@mui/material/styles";
import {FaCar, FaUtensils} from "react-icons/fa";
import MyPage from "./eats/MyPage";
import RestaurantRegistration from "./eats/RestaurantRegistration";
import RestaurantManage from "./eats/RestaurantManage";
import RestaurantList from "./eats/RestaurantList";
import RestaurantPage from "./eats/RestaurantPage";
import MenuPage from "./eats/MenuPage";
import CheckoutPage from "./eats/CheckoutPage";
import {CheckoutSuccessPage} from "./eats/checkout/CheckoutSuccessPage";
import {CheckoutFailPage} from "./eats/checkout/CheckoutFailPage";
import RestaurantListFromSearch from "./eats/RestaurantListFromSearch";
import {useSelector} from "react-redux";
import Login from "./Login";
import {getAuthToken} from "./eats/utils/auth";

const IconContainer = styled(Box)({
   display: "flex",
   justifyContent: "center",
   alignItems: "center",
});

const IconLink = styled(Link)({
   display: "flex",
   flexDirection: "column",
   alignItems: "center",
   textDecoration: "none",
   color: "#000",
   margin: "0 20px",
   padding: "20px",
   borderRadius: "25px",
   border: "2px solid #fff",
   transition: "background-color 0.3s ease",
   "&:hover": {
      backgroundColor: "rgba(0, 0, 0, 0.1)",
   },
});

const IconWrapper = styled(Box)({
   fontSize: "7rem",
   marginBottom: "10px",
});
const PrivateRoute = ({children}) => {
   const [isAuthenticated, setIsAuthenticated] = useState(false);
   const [isLoading, setIsLoading] = useState(true);

   useEffect(() => {
      const checkAuth = async () => {
         const token = await getAuthToken();
         setIsAuthenticated(!!token);
         setIsLoading(false);

         if (!token) {
            // 인증되지 않은 경우 백엔드 서버로 리다이렉트
            window.location.href = 'http://localhost:8083';
         }
      };
      checkAuth();
   }, []);

   if (isLoading) {
      return <div>Loading...</div>; // 또는 로딩 스피너 컴포넌트
   }

   return isAuthenticated ? children : null;
};


const router = createBrowserRouter([
   {
      path: "/login",
      element: <Login/>,
   },
   {
      path: "/",
      element: (
         <PrivateRoute>
            <Grid container display="flex" justifyContent="center" alignItems="center">
               <Grid container style={{justifyContent: "flex-center", flexDirection: "column"}}>
                  <Typography style={{fontSize: "2.5rem", fontWeight: "bold", marginBottom: "2rem"}}>
                     Choose a Service
                  </Typography>
               </Grid>
               <Grid container style={{justifyContent: "flex-end", flexDirection: "column"}}>
                  <IconContainer>
                     <IconLink to="/uber">
                        <IconWrapper>
                           <FaCar/>
                        </IconWrapper>
                        <Typography variant="h5">Taxi</Typography>
                     </IconLink>
                     <IconLink to="/eats">
                        <IconWrapper>
                           <FaUtensils/>
                        </IconWrapper>
                        <Typography variant="h5">Delivery</Typography>
                     </IconLink>
                  </IconContainer>
               </Grid>
            </Grid>

         </PrivateRoute>
      ),
   },
   {
      path: "/uber",
      element: <UberLikeApp/>,
   },
   {
      path: "/eats",
      element: <EatsMain/>,
   },
   {
      path: "/eats/mypage",
      element: <MyPage/>,
   },
   {
      path: "/eats/restaurant-registration",
      element: <RestaurantRegistration/>,
   },
   {
      path: "/eats/restaurant-manage",
      element: <RestaurantManage/>,
   },
   {
      path: "/eats/restaurants/:type",
      element: <RestaurantList/>,
   },
   {
      path: "/eats/search/restaurants",
      element: <RestaurantListFromSearch/>,
   },
   {
      path: "/eats/restaurant/restaurant-page",
      element: <RestaurantPage/>,
   },
   {
      path: "/eats/restaurant/menu",
      element: <MenuPage/>,
   },
   {
      path: "/eats/checkout",
      element: <CheckoutPage/>,
   },
   {
      path: "/eats/checkout/success",
      element: <CheckoutSuccessPage/>,
   },
   {
      path: "/eats/checkout/fail",
      element: <CheckoutFailPage/>,
   }
]);


export default function App() {
   return (
      <RouterProvider router={router}/>
   );
}


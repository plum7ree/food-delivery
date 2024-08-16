import React, {useEffect} from "react";
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
import {useDispatch, useSelector} from "react-redux";
import Login from "./Login";
import Register from "./eats/Register";
import {asyncGetAuth} from "./state/authSlice";
// import {PersistGate} from "redux-persist/integration/react";
// import PostToken from "./ReceiveGoogleOAuth2Token(googleclientsidenotworking)";
// import ReceiveGoogleOAuth2TokenGoogleclientsidenotworking from "./ReceiveGoogleOAuth2Token(googleclientsidenotworking)";

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
   const dispatch = useDispatch();
   const getAuthStatus = useSelector((state) => state.auth.getAuthStatus);
   const isLoggedIn = useSelector((state) => state.auth.isLoggedIn);

   useEffect(() => {
      if (getAuthStatus === 'idle') {
         console.log('idle')
         dispatch(asyncGetAuth());
      }
   }, [dispatch, getAuthStatus]);

   if (getAuthStatus === 'pending') {
      console.log('pending')
      return <div>Loading...</div>;
   }

   if (getAuthStatus === 'rejected') {
      console.log('rejected')

      return <Navigate to="/login"/>;
   }

   return isLoggedIn ? children : <Navigate to="/login"/>;
};
const router = createBrowserRouter([
   {
      path: "/login",
      element: <Login/>,
   },
   // {
   //    path: "/token",
   //    element: <ReceiveGoogleOAuth2TokenGoogleclientsidenotworking/>,
   // },
   {
      path: "/register",
      element: <Register/>,
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
   const dispatch = useDispatch();
   const isLoggedIn = useSelector((state) => state.auth?.isLoggedIn ?? false);
   const credential = useSelector((state) => state.auth?.credential ?? null);

   useEffect(() => {
      if (isLoggedIn && credential) {
         dispatch({type: 'notifications/connect'});
      }

      return () => {
         dispatch({type: 'notifications/disconnect'});
      };
   }, [isLoggedIn, credential, dispatch]);

   return (
      <RouterProvider router={router}/>
   );
}

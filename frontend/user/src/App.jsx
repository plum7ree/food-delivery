import React, {useEffect} from "react";
import {createBrowserRouter, Link, Navigate, RouterProvider} from "react-router-dom";
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
import {toast, ToastContainer} from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import DriverMap from "./eats/driver/DriverMap";

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

/**
 * React-Redux의 useSelector 훅:
 * useSelector는 Redux 스토어의 상태를 구독합니다. 선택된 상태가 변경될 때마다 컴포넌트를 다시 렌더링합니다.
 * 상태 변경 감지:
 * Redux 스토어에서 isLoggedIn 상태가 변경되면, useSelector가 이를 감지합니다.
 * 컴포넌트 리렌더링:
 * 상태 변경이 감지되면, React는 PrivateRoute 컴포넌트를 리렌더링합니다.
 * 조건부 렌더링:
 * 리렌더링 시 새로운 isLoggedIn 값에 따라 적절한 내용(자식 컴포넌트 또는 리다이렉트)을 렌더링합니다.
 *
 * @param children
 * @returns {*|React.JSX.Element}
 * @constructor
 */
const PrivateRoute = ({children}) => {
   const getAuthStatus = useSelector((state) => state.auth.getAuthStatus);
   const isLoggedIn = useSelector((state) => state.auth.isLoggedIn);

   if (getAuthStatus === 'pending') {
      console.log('pending')
      return <div>Loading...</div>;
   }

   if (getAuthStatus === 'rejected') {
      console.log('rejected')

      return <Navigate to="/login"/>;
   }

   if (getAuthStatus === 'fulfilled') {
      return isLoggedIn ? children : <Navigate to="/login"/>;
   }
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
   const getAuthStatus = useSelector((state) => state.auth.getAuthStatus);

   useEffect(() => {
      if (getAuthStatus === 'idle') {
         dispatch(asyncGetAuth());
      }
   }, [dispatch, getAuthStatus]);

   useEffect(() => {
      if (isLoggedIn && credential) {
         dispatch({type: 'notifications/connect'});
      }

      return () => {
         dispatch({type: 'notifications/disconnect'});
      };
   }, [isLoggedIn, credential, dispatch]);


   useEffect(() => {
      if (isLoggedIn && credential) {
         dispatch({type: 'notifications/connect'});
      }

      return () => {
         dispatch({type: 'notifications/disconnect'});
      };
   }, [isLoggedIn, credential, dispatch]);

   return (
      <>
         <RouterProvider router={router}/>
         <DriverMap />
         <ToastContainer autoClose={2000} stacked />
      </>
   );
}

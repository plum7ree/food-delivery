import React, {useEffect, useState} from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import axiosInstance from "../state/axiosInstance";
import {Avatar, Grid, IconButton, Typography, styled, Rating} from "@mui/material";
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import SearchIcon from '@mui/icons-material/Search';
import {Tabs, Tab} from "@mui/material";
import {Container} from "@mui/system";
import {
   GetGroupedRestaurantsByType as getMockRestaurants
} from './resources/RestaurantListTestData';
import restaurantData from "./resources/web_crawled_restaurant_data.json";
import restaurantType from "./RestaurantType";
import RestaurantLabel from "./RestaurantLabel"; // restaurants 데이터 가져오기

const StyledTab = styled(Tab)(({theme}) => ({
   '&.Mui-selected .MuiAvatar-root': {
      border: `2px solid ${theme.palette.primary.main}`, // 선택 됬을때 avatar checkout logo 테두리 primary 색갈로 바꿈.
   },
   '&.Mui-selected .MuiTypography-root': {
      color: theme.palette.primary.main,
   },
}));

const RestaurantList = () => {
   const [restaurantTypeSet, setRestaurantTypeSet] = useState(new Set());
   const [restaurants, setRestaurants] = useState({});
   const [currentTypeRestaurants, setCurrentTypeRestaurants] = useState([]);
   const [type, setType] = useState(null);
   const [selectedTab, setSelectedTab] = useState(0);
   const [selectedLogoTab, setSelectedLogoTab] = useState(0);
   const location = useLocation();
   const navigate = useNavigate();


   const isTestMode = true; // 테스트 플래그 변수

   useEffect(() => {
      //TODO 모든 타입을 한번 fetch, 그 다음 해당 탭으로 넘어가면, 해당 타입 레스토랑들을 페이지 형식으로 update.
   }, [])

   useEffect(() => {
      setRestaurantTypeSet(new Set(restaurantData.map(restaurant => restaurant.type)));
      console.log(restaurantTypeSet)
   }, [])

   useEffect(() => {
      var typeParam = location.state['type']
      if (typeParam) {
         setType(typeParam);
      }
   }, [location]);

   useEffect(() => {
      if (isTestMode) {
         // 테스트 모드인 경우 가짜 데이터 사용
         setRestaurants(getMockRestaurants());
         console.log(getMockRestaurants())
      } else {
         // 실제 모드인 경우 Axios 요청으로 데이터 가져오기
         const fetchRestaurantsByType = async () => {
            try {
               const response = await axiosInstance.get(`/user/api/seller/restaurants?type=${type}`);
               console.log(response.data)
               let newRestaurants = restaurants;
               newRestaurants[type].extend(response.data.content)
               setRestaurants(newRestaurants);
            } catch (error) {
               console.error("Error fetching restaurants:", error);
            }
         };
         fetchRestaurantsByType();
      }
   }, [type, isTestMode]); // hook. type or isTestMode

   useEffect(() => {
      console.log(type)
      setCurrentTypeRestaurants(restaurants[type])
      console.log(restaurants[type])
   }, [type])


   useEffect(() => {
      if (type) {
         const fetchRestaurantsByType = async () => {
            try {
               const response = await axiosInstance.get(`/user/api/seller/restaurants?type=${type}`);
               console.log(response.data)
               let newRestaurants = restaurants;
               newRestaurants[type].extend(response.data.content)
               setRestaurants(newRestaurants);
            } catch (error) {
               console.error("Error fetching restaurants:", error);
            }
         };
         fetchRestaurantsByType();
      }
   }, [type]);


   const handleRestaurantClick = (restaurant) => {
      console.log(restaurant)
      navigate(`/eats/restaurant/restaurant-page`, {
         state: {
            restaurant: restaurant
         }
      });
   };

   const handleTabChange = (event, newValue) => {
      console.log(newValue)
      setSelectedTab(newValue);
      setType(newValue)
      // 선택한 탭에 따라 type 값을 변경하고 해당하는 레스토랑 목록을 가져오는 로직을 구현합니다.
      // ...
   };

   const handleLogoTabChange = (event, newValue) => {
      setSelectedLogoTab(newValue);
      // 선택한 로고 탭에 따라 해당하는 레스토랑 목록을 가져오는 로직을 구현합니다.
      // ...
   };

   return (
      <Container maxWidth="sm">
         <Grid container direction="column" spacing={2}>
            <Grid item container alignItems="center" justifyContent="space-between">
               <IconButton onClick={() => {
                  navigate(-1)
               }} color="inherit" aria-label="menu">
                  <ArrowBackIcon/>
               </IconButton>
               <Typography variant="h6">{type}</Typography>
               <IconButton>
                  <SearchIcon/>
               </IconButton>
            </Grid>
            <Grid item container>
               <Tabs value={selectedTab} onChange={handleTabChange} variant="scrollable" scrollButtons="auto">
                  {[...restaurantTypeSet].map((type, index) => (
                     <Tab key={index} label={type} value={type}/>
                  ))}
               </Tabs>
            </Grid>
            <Grid item container spacing={2} direction="column" style={{overflowY: 'scroll'}}>
               {currentTypeRestaurants && currentTypeRestaurants.map((restaurant) => {
                  return (
                     <RestaurantLabel key={restaurant.id}
                                      restaurant={restaurant}
                                      handleRestaurantClick={handleRestaurantClick}
                     />
                  )
               })}
            </Grid>
         </Grid>
      </Container>
   );
};

export default RestaurantList;
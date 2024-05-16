import React, {useEffect, useState} from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import axiosInstance from "../state/axiosInstance";
import {Avatar, Grid, IconButton, Typography, styled, Rating} from "@mui/material";
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import SearchIcon from '@mui/icons-material/Search';
import {Tabs, Tab} from "@mui/material";
import {Container} from "@mui/system";
import {RestaurantListTestData as mockRestaurants} from './resources/RestaurantListTestData'; // restaurants 데이터 가져오기

const StyledTab = styled(Tab)(({theme}) => ({
   '&.Mui-selected .MuiAvatar-root': {
      border: `2px solid ${theme.palette.primary.main}`, // 선택 됬을때 avatar checkout logo 테두리 primary 색갈로 바꿈.
   },
   '&.Mui-selected .MuiTypography-root': {
      color: theme.palette.primary.main,
   },
}));

const RestaurantListFromSearch = () => {
  const [restaurantIds, setRestaurantIds] = useState([]);
   const [restaurants, setRestaurants] = useState([]);
   const [type, setType] = useState(null);
   const [selectedTab, setSelectedTab] = useState(0);
   const [selectedLogoTab, setSelectedLogoTab] = useState(0);
   const location = useLocation();
   const navigate = useNavigate();


   const isTestMode = false; // 테스트 플래그 변수


   useEffect(() => {
      var _restaurantIds = location.state['restaurantIds']
      if (_restaurantIds) {
         setRestaurantIds(_restaurantIds);
      }
   }, [location]);

  useEffect(() => {
    const fetchRestaurants = async () => {
      if (isTestMode) {
        // 테스트 모드인 경우 가짜 데이터 사용
        setRestaurants(mockRestaurants);
      } else {
        // 실제 모드인 경우 Axios 요청으로 데이터 가져오기
        try {
          const promises = restaurantIds.map((restaurantId) =>
            axiosInstance.get(`/user/api/seller/restaurant/${restaurantId}`)
          );
          const responses = await Promise.all(promises);
          const fetchedRestaurants = responses.map((response) => response.data.content);
          setRestaurants(fetchedRestaurants);
        } catch (error) {
          console.error("Error fetching restaurants:", error);
        }
      }
    };

    fetchRestaurants();
  }, [restaurantIds, isTestMode]);


   const handleCategoryClick = (restaurant) => {
      console.log(restaurant)
      navigate(`/eats/restaurant/restaurant-page`, {
         state: {
            restaurantId: restaurant.id
         }
      });
   };

   const handleTabChange = (event, newValue) => {
      setSelectedTab(newValue);
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
            <Grid item container spacing={2} direction="column" style={{overflowY: 'scroll'}}>
               {restaurants.map((restaurant) => (
                  <Grid item key={restaurant.id} onClick={() => handleCategoryClick(restaurant)}>
                     <Grid container spacing={2} alignItems="center">
                        <Grid item xs={4}>
                           <img src={restaurant.imageUrl} alt={restaurant.name}
                                style={{width: '100%', height: 'auto'}}/>
                        </Grid>
                        <Grid item xs={8}>
                           <Typography variant="subtitle1" align="left">{restaurant.name}</Typography>
                           <Grid container alignItems="center" spacing={1}>
                              <Grid item>
                                 <Rating name="half-rating" defaultValue={2.5} precision={0.5} max={1}/>
                              </Grid>
                              <Grid item>
                                 <Typography variant="body2">{restaurant.rating}</Typography>
                              </Grid>
                              <Grid item>
                                 <Typography variant="body2">{restaurant.distance} km</Typography>
                              </Grid>
                              <Grid item>
                                 <Typography variant="body2">{restaurant.deliveryTime} 분</Typography>
                              </Grid>
                              <Grid item>
                                 <Typography variant="body2">{restaurant.deliveryFee} 원</Typography>
                              </Grid>
                           </Grid>
                        </Grid>
                     </Grid>
                  </Grid>
               ))}
            </Grid>
         </Grid>
      </Container>
   );
};

export default RestaurantListFromSearch;
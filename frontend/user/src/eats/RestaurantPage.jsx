import React, {useEffect, useState} from 'react';
import {Link, useLocation, useNavigate} from 'react-router-dom';
import {Box, Grid, Typography, Divider, IconButton, Rating, Tabs, Tab, Button, Badge} from "@mui/material";
import {ArrowBack, Call, FavoriteBorder, Home, Share, ShoppingCart} from "@mui/icons-material";
import axiosInstance from "../state/axiosInstance";
import {v4 as uuidv4} from 'uuid';
import {SearchRestaurantTestData as mockSearchRestaurant} from "./resources/RestaurantTestData";
import {Container, maxWidth} from "@mui/system";
import {selectCartItemCount} from "../state/checkout/selectedMenuSlice";
import {useDispatch, useSelector} from "react-redux";
import {initRestaurantIfNeeded} from "../state/checkout/selectedMenuSlice";
import ReviewComponent from "./ReviewComponent";
import ReviewsTestData from "./resources/ReviewsTestData";

const RestaurantPicture = (props) => {
   const {pictureUrl1, type} = props.restaurant;
   const navigate = useNavigate();

   return (
      <Grid container item>
         <Grid container item justifyContent="space-between">
            <Grid item>
               <IconButton onClick={() => {
                  navigate(-1)
               }}>
                  <ArrowBack/>
               </IconButton>
            </Grid>
            <Grid item>
               <IconButton>
                  <Home/>
               </IconButton>
            </Grid>
         </Grid>

         <Grid container item justifyContent="center">
            <img src={pictureUrl1} style={{width: '324px', height: '200'}}/>

         </Grid>

      </Grid>
   );
};

const RestaurantInfo = ({restaurant}) => {
   return (
      // 왜 여기 Grid container 로 하면 중앙으로 정렬 안되지?
      <Grid container item justifyContent="center" spacing={0}>
         <Grid item>
            <Typography variant="h4">{restaurant.name}</Typography>
         </Grid>
         <Grid item container justifyContent="center" direction="row">
            <Grid item>
               <Rating name="half-rating" defaultValue={4.5} precision={0.5} max={5}/>
            </Grid>
            <Grid item>
               <Typography variant="subtitle1">4.5</Typography>
            </Grid>
         </Grid>
         <Grid item container justifyContent="center" direction="row">
            <Grid item mr={1}>
               <Typography variant="body2" color="text.secondary">Reviews: 50</Typography>
            </Grid>
            <Grid item mr={1}>
               <Typography variant="body2" color="text.secondary">|</Typography>
            </Grid>
            <Grid item>
               <Typography variant="body2" color="text.secondary">Comments: 25</Typography>
            </Grid>
         </Grid>
      </Grid>
   );
};
const RestaurantActions = ({onCheckout}) => {
   const cartItemCount = useSelector(selectCartItemCount);
   return (
      <Grid container item justifyContent="space-between">
         <Grid item>
            <IconButton>
               <Call/>
            </IconButton>
         </Grid>
         <Grid item>
            <IconButton>
               <FavoriteBorder/>
            </IconButton>
         </Grid>
         <Grid item>
            <IconButton>
               <Share/>
            </IconButton>
         </Grid>
         <Grid item>
            <IconButton onClick={() => onCheckout()}>
               {cartItemCount > 0 && (
                  <Badge badgeContent={cartItemCount} color="error">
                     <ShoppingCart/>
                  </Badge>
               )}
               {cartItemCount === 0 && <ShoppingCart/>}
            </IconButton>
         </Grid>
      </Grid>
   );
};

const RestaurantMenu = ({restaurantId, menus, onOptionSelect}) => {
   const navigate = useNavigate();
   const dispatch = useDispatch();

   const goToMenuPage = (menu) => {
      console.log(menu)
      dispatch(initRestaurantIfNeeded({restaurantId: restaurantId}));
      navigate(`/eats/restaurant/menu`, {
         state: {
            menu,
            onOptionSelect: onOptionSelect,
         },
      });
   };
   return (
      <Grid container item direction="column" alignItems="center" spacing={3}>
         {menus.map((menu) => (
            <Grid container key={uuidv4()} item direction="row" justifyContent="space-between"
                  onClick={() => goToMenuPage(menu)}>
               <Grid item>
                  <Grid item>
                     <Typography variant="h5">{menu.name}</Typography>
                  </Grid>
                  <Grid item>
                     <Typography variant="subtitle1">{menu.price}</Typography>
                  </Grid>
               </Grid>
               <Grid item>
                  <img src={menu.pictureUrl} style={{width: '162px', height: '100px'}}/>
               </Grid>
            </Grid>
         ))}
      </Grid>
   );
};

const RestaurantPage = () => {
   const location = useLocation();
   const navigate = useNavigate();
   const [restaurantIdState, setRestaurantIdState] = useState({});
   const [restaurantState, setRestaurantState] = useState({});
   const [currentTab, setCurrentTab] = useState(0); // 0 for Menu, 1 for Reviews
   const [total, setTotal] = useState(0);

   // review example
   const [reviews, setReviews] = useState([]);
   const [reviewCount, setReviewCount] = useState(0);
   const [rating, setRating] = useState(0);
   const [ratingDistribution, setRatingDistribution] = useState({});


   const isTestMode = false;

   useEffect(() => {
      const restaurant = location.state['restaurant']
      if (isTestMode) {
         const mockRestaurant = restaurant
         console.log(mockRestaurant.menuDtoList)
         setRestaurantState(mockRestaurant);
         setRestaurantIdState(restaurant.id);

         const reviewList = ReviewsTestData.get(restaurant.id)
         setReviews(reviewList);
         setReviewCount(reviewList.length)
         setRating(5.0);
         setReviewCount(503);
         setRatingDistribution({
            5: 98,
            4: 1,
            3: 1,
            2: 0,
            1: 0
         })

      } else {
         setRestaurantIdState(restaurant.id);

      }
   }, []);


   useEffect(() => {
      const fetchRestaurantContent = async (restaurantId) => {
         try {
            const response = await axiosInstance.get(`/user/api/seller/restaurant/${restaurantId}`);
            console.log(response.data)
            setRestaurantState(response.data);
         } catch (error) {
            console.error('Error fetching restaurant data:', error);
         }
      };
      fetchRestaurantContent(restaurantIdState);
   },[restaurantIdState]);

   const handleTabChange = (event, newValue) => {
      setCurrentTab(newValue);
   };


   const goToCheckoutPage = () => {
      navigate('/eats/checkout');
   };

   return (
      <Container maxWidth="sm">
         <Grid container direction="column" spacing={4} justifyItems="center">
            <RestaurantPicture restaurant={restaurantState}/>
            <RestaurantInfo restaurant={restaurantState}/>
            <Grid item>
               <Grid item>
                  <Divider/>
               </Grid>
               <Grid item>
                  <RestaurantActions onCheckout={goToCheckoutPage}/>
               </Grid>
               <Grid item>
                  <Divider/>
               </Grid>
            </Grid>
            <Grid container item>
               <Tabs value={currentTab} onChange={handleTabChange} centered>
                  <Tab label="Menu"/>
                  <Tab label="Reviews"/>
               </Tabs>
            </Grid>
            <Grid container item>
               {currentTab === 0 && (
                  <Grid container>
                     {restaurantState.menuDtoList && (
                        <RestaurantMenu
                           restaurantId={restaurantIdState}
                           menus={restaurantState.menuDtoList}
                        />
                     )}
                  </Grid>
               )}
               {currentTab === 1 && (
                  <Grid container>
                     <ReviewComponent
                        reviews={reviews}
                        rating={rating}
                        reviewCount={reviewCount}
                        ratingDistribution={ratingDistribution}
                     />
                  </Grid>
               )}
            </Grid>
         </Grid>
      </Container>
   );
};

export default RestaurantPage;
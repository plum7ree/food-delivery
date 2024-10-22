import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from "react-redux";
import { fetchProfilePicture } from "../state/fetchProfilePicture";
import { Grid, Typography, TextField, IconButton, Autocomplete } from '@mui/material';
import { styled } from '@mui/material/styles';
import SearchIcon from '@mui/icons-material/Search';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import { Container } from "@mui/system";
import axiosInstance from "../state/axiosInstance";
import RestaurantType from "./RestaurantType";
import RestaurantLabelLargePicture from "./RestaurantLabelLargePicture";
import { fetchRestaurantsByType } from "../state/restaurantSlice";

const SearchBar = styled(Grid)(({ theme }) => ({
   display: 'flex',
   alignItems: 'center',
   backgroundColor: theme.palette.background.paper,
   padding: theme.spacing(1),
   borderRadius: theme.shape.borderRadius,
}));

const CategoryLink = styled(Link)({
   textDecoration: 'none',
   color: 'inherit',
   '&:hover': {
      textDecoration: 'none',
   },
});

// Move the categories array outside of the EatsMain component
const categories = [
   { name: '햄버거', icon: '🍔', type: RestaurantType.BURGER },
   { name: '피자', icon: '🍕', type: RestaurantType.PIZZA },
   { name: '한식', icon: '🍲', type: RestaurantType.KOREAN },
   { name: '중식', icon: '🍜', type: RestaurantType.CHINESE },
   { name: '일식', icon: '🍣', type: RestaurantType.JAPANESE },
   { name: '멕시칸', icon: '🌮', type: RestaurantType.MEXICAN },
   { name: '이탈리안', icon: '🍝', type: RestaurantType.ITALIAN },
   { name: '미국식', icon: '🍗', type: RestaurantType.AMERICAN },
   { name: '퓨전', icon: '🥗', type: RestaurantType.FUSION },
   { name: '기타', icon: '🍽️', type: RestaurantType.MISC },
];

const EatsMain = () => {
   const dispatch = useDispatch();
   const profilePictureUrl = useSelector((state) => state.profilePicture.url);
   const [searchResult, setSearchResult] = useState([]);
   const [recommendedRestaurantNamesForSearch, setRecommendedRestaurantNamesForSearch] = useState([]);
   const [restaurantsFromSearchResult, setRestaurantsFromSearchResult] = useState([]);
   const [searchText, setSearchText] = useState('');
   const navigate = useNavigate();
   const restaurantListByTypeState = useSelector((state) => state.restaurants.restaurantListByType);

   // TODO scroll 기능
   useEffect(() => {
      categories.forEach((category) => {
         dispatch(fetchRestaurantsByType({ type: category.type, limit: 10 }));
      });

   }, [dispatch]); // Only dispatch is needed here, categories is now stable.

   useEffect(() => {
      // dispatch(fetchProfilePicture());
   }, [dispatch]);

   useEffect(() => {
      const restaurantsNames = searchResult.map((restaurant) => restaurant.name);
      setRecommendedRestaurantNamesForSearch(restaurantsNames);
      setRestaurantsFromSearchResult(searchResult);
   }, [searchResult]);


   const handleCategoryClick = (categoryType) => {
      navigate(`/eats/restaurants/${categoryType}`, {
         state: {
            type: `${categoryType}`
         }
      });
   };

   const handleSearchChange = async (event, value) => {
      setSearchText(value);

      if (value) {
         try {
            const response = await axiosInstance.get(`/eatssearch/api/search?text=${value}`);
            console.log(response.data);
            setSearchResult(response.data);
         } catch (error) {
            console.error('Error fetching search suggestions:', error);
         }
      } else {
         setSearchResult([]);
      }
   };

   const handleSearchSubmit = () => {
      if (searchText) {
         navigate(`/eats/search/restaurants`, {
            state: {
               restaurants: restaurantsFromSearchResult
            }
         });
      }
   };

   const handleRestaurantClick = (restaurant) => {
      navigate(`/eats/restaurant/restaurant-page`, {
         state: {
            restaurant: restaurant
         }
      });
   };

   return (
      <Container maxWidth="sm">
         <Grid container direction="column" spacing={2}>
            <Grid container item>
               <Grid container direction="row" alignItems="center" justifyContent="space-between">
                  <SearchBar item sx={{ flexGrow: 1, marginRight: 1 }}>
                     <SearchIcon />
                     <Autocomplete
                        sx={{ flexGrow: 1 }}
                        freeSolo
                        options={recommendedRestaurantNamesForSearch}
                        onInputChange={handleSearchChange}
                        onChange={handleSearchSubmit}
                        renderInput={(params) => (
                           <TextField {...params} placeholder="음식점 또는 음식 검색" fullWidth />
                        )}
                     />
                  </SearchBar>
                  <IconButton component={Link} to="/eats/mypage">
                     {profilePictureUrl ? (
                        <img src={profilePictureUrl} alt="Profile" />
                     ) : (
                        <AccountCircleIcon fontSize="large" />)}
                  </IconButton>
               </Grid>
            </Grid>

            <Grid item mb={3} justifyContent="center" alignItems="center">
               <Typography variant="h6" mb={3}>인기 카테고리</Typography>
               <Grid container spacing={2} mb={3} alignItems="center" justifyContent="space-between">
                  {categories.map((category, index) => (
                     <Grid item key={index} component={CategoryLink} to={`/eats/restaurants/${category.type}`}
                           state={{ type: category.type }}>
                        <Grid container direction="column" alignItems="center">
                           <Typography variant="h4">{category.icon}</Typography>
                           <Typography>{category.name}</Typography>
                        </Grid>
                     </Grid>
                  ))}
               </Grid>
            </Grid>

            <Grid container item justifyContent="center" alignItems="center">
               <Typography variant="h6" mb={3}>인기 음식점</Typography>
               {Object.entries(restaurantListByTypeState).map(([type, restaurantList]) => (
                  <Grid key={type} item container spacing={5} justifyContent="center" alignItems="center"
                        direction="column">
                     {restaurantList.map((restaurant, i) => (
                        <RestaurantLabelLargePicture
                           key={restaurant.id}
                           restaurant={restaurant}
                           handleRestaurantClick={handleRestaurantClick}
                        />
                     ))}
                  </Grid>
               ))}
            </Grid>
         </Grid>
      </Container>
   );
};

export default EatsMain;

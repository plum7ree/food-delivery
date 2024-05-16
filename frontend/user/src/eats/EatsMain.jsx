import React, {useEffect, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {useDispatch, useSelector} from "react-redux";
import {fetchProfilePicture} from "../state/fetchProfilePicture";
import {Grid, Typography, TextField, IconButton, Autocomplete} from '@mui/material';
import {styled} from '@mui/material/styles';
import SearchIcon from '@mui/icons-material/Search';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import {Container} from "@mui/system";
import axiosInstance from "../state/axiosInstance";

const SearchBar = styled(Grid)(({theme}) => ({
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

const EatsMain = () => {
   const categories = [
      {name: '피자', icon: '🍕', type: 'pizza'},
      {name: '치킨', icon: '🍗', type: 'chicken'},
      {name: '햄버거', icon: '🍔', type: 'BURGER'},
      {name: '아시안', icon: '🍜', type: 'asian'},
      {name: '멕시코', icon: '🌮', type: 'mexican'},
      {name: '디저트', icon: '🍰', type: 'desert'},
   ];

   const dispatch = useDispatch();
   const profilePictureUrl = useSelector((state) => state.profilePicture.url);
   const [searchResult, setSearchResult] = useState([]);
   const [recommendedRestaurantNamesForSearch, setRecommendedRestaurantNamesForSearch] = useState([]);
   const [restaurantIdsFromSearchResult, setRestaurantIdsFromSearchResult] = useState([]);
   const [searchText, setSearchText] = useState('');
   const navigate = useNavigate();


   useEffect(() => {
      dispatch(fetchProfilePicture());
   }, [dispatch]);

   useEffect(() => {
      const restaurantsNames = searchResult.map((restaurant) => restaurant.name);
      setRecommendedRestaurantNamesForSearch(restaurantsNames)

      const restaurantIds = searchResult.map((restaurant) => restaurant.id);
      setRestaurantIdsFromSearchResult(restaurantIds)
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
            console.log(response.data)
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
               restaurantIds: restaurantIdsFromSearchResult
            }
         });
      }
   };

   return (
      <Container maxWidth="sm">
         <Grid container direction="column" spacing={2}>
            <Grid container item>
               <Grid container direction="row" alignItems="center" justifyContent="space-between">
                  <SearchBar item sx={{flexGrow: 1, marginRight: 1}}>
                     <SearchIcon/>
                     {/*<TextField placeholder="음식점 또는 음식 검색" fullWidth />*/}
                     <Autocomplete
                        sx={{flexGrow: 1}}
                        freeSolo
                        options={recommendedRestaurantNamesForSearch}
                        onInputChange={handleSearchChange}
                        onChange={handleSearchSubmit}
                        renderInput={(params) => (
                           <TextField {...params} placeholder="음식점 또는 음식 검색" fullWidth/>
                        )}
                     />
                  </SearchBar>
                  <IconButton component={Link} to="/eats/mypage">
                     {profilePictureUrl ? (
                        <img src={profilePictureUrl} alt="Profile"/>
                     ) : (
                        <AccountCircleIcon fontSize="large"/>
                     )}
                  </IconButton>
               </Grid>
            </Grid>

            <Grid item>
               <Typography variant="h6" mb={3}>인기 카테고리</Typography>
               <Grid container spacing={2} mb={3} alignItems="center" justifyContent="space-between">
                  {categories.map((category, index) => (
                     <Grid item key={index} component={CategoryLink} to={`/eats/restaurants/${category.type}`}
                           state={{type: category.type}}>
                        <Grid container direction="column" alignItems="center">
                           <Typography variant="h4">{category.icon}</Typography>
                           <Typography>{category.name}</Typography>
                        </Grid>
                     </Grid>
                  ))}
               </Grid>
            </Grid>

            <Grid item>
               <Typography variant="h6">인기 음식점</Typography>
               {/* 인기 음식점 목록 */}
            </Grid>
         </Grid>


      </Container>
   );
};

export default EatsMain;
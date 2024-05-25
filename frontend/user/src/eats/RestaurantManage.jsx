import React, {useEffect, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {Box, Fab, Grid, IconButton, List, ListItem, ListItemText, Typography} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import axiosInstance from "../state/axiosInstance";
import {Container} from "@mui/system";
import {ArrowBack, Home} from "@mui/icons-material";
import { useTheme } from '@mui/material/styles';

const RestaurantManage = () => {
   const [restaurants, setRestaurants] = useState([]);
   const theme = useTheme();
   const navigate = useNavigate();

   useEffect(() => {
      fetchRegisteredRestaurants();
   }, []);

   const fetchRegisteredRestaurants = async () => {
      try {
         const response = await axiosInstance.get('/user/api/seller/user-registered-restaurant');
         if (Array.isArray(response.data)) {
            setRestaurants(response.data);
         } else {
            console.error('Response data is not an array:', response.data);
            setRestaurants([]);
         }
      } catch (error) {
         console.error('Error fetching registered restaurants:', error);
         setRestaurants([]);
      }
   };

   return (
      <Container maxWidth="sm">
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
          <Grid item sx={{  padding: 2 }}>
             <Typography variant="h5">레스토랑 관리</Typography>
          </Grid>
          <Grid item sx={{  padding: 2 }}>
         <List>
            {restaurants.map((restaurant, index) => (
               <ListItem
                  key={index}
                  button
                  component={Link}
                  to={`/restaurant/${restaurant.id}`}
               >
                  <ListItemText primary={restaurant.name} secondary={restaurant.type}/>
               </ListItem>
            ))}
         </List>
          </Grid>
         <Grid container item justifyContent="flex-end">
         <Fab
            color="primary"
            component={Link}
            to="/eats/restaurant-registration"
            aria-label="add"

         >
            <AddIcon/>
         </Fab>

         </Grid>
      </Container>
   );
};

export default RestaurantManage;
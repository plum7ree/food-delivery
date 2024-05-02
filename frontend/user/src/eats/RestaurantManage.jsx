import React, {useEffect, useState} from 'react';
import {Link} from 'react-router-dom';
import {Box, Fab, List, ListItem, ListItemText} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import axiosInstance from "../state/axiosInstance";

const RestaurantManage = () => {
   const [restaurants, setRestaurants] = useState([]);

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
      <div>
         <Box sx={{position: 'fixed', bottom: 16, right: 16}}>
            <Fab
               color="primary"
               component={Link}
               to="/eats/restaurant-registration"
               aria-label="add"
            >
               <AddIcon/>
            </Fab>
         </Box>
         <h2>등록된 레스토랑 목록</h2>
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
      </div>
   );
};

export default RestaurantManage;
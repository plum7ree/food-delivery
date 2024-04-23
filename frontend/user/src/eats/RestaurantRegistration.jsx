import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  Box,
  Typography,
  Button,
  TextField,
  Grid,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from '@mui/material';
import {useSelector} from "react-redux";

const RestaurantRegistration = () => {
  const [sessionId, setSessionId] = useState('');
  const [restaurantName, setRestaurantName] = useState('');
  const [restaurantType, setRestaurantType] = useState('');
  const [openTime, setOpenTime] = useState('');
  const [closeTime, setCloseTime] = useState('');
  const [pictureFile, setPictureFile] = useState(null);
  const [registeredRestaurants, setRegisteredRestaurants] = useState([]);
  const axiosInstance = useSelector((state) => state.axiosInstance.instance);

  useEffect(() => {
    fetchRegisteredRestaurants();
  }, []);

  const createSession = async () => {
    try {
      const response = await axiosInstance.post('/user/api/seller/create-session');
      setSessionId(response.data);
    } catch (error) {
      console.error('Error creating session:', error);
    }
  };

  const uploadPicture = async () => {
    try {
      const formData = new FormData();
      formData.append('file', pictureFile);
      formData.append('sessionId', sessionId);
      await axiosInstance.post('/user/api/seller/restaurant-picture-resized', formData);
    } catch (error) {
      console.error('Error uploading picture:', error);
    }
  };

  const registerRestaurant = async () => {
    try {
      const restaurantData = {
        name: restaurantName,
        type: restaurantType,
        openTime: openTime,
        closeTime: closeTime,
      };
      await axiosInstance.post(`/user/api/seller/register/restaurant?sessionId=${sessionId}`, restaurantData);
      fetchRegisteredRestaurants();
    } catch (error) {
      console.error('Error registering restaurant:', error);
    }
  };

  const fetchRegisteredRestaurants = async () => {
    try {
      const response = await axiosInstance.get('/user/api/seller/registered-restaurant');
      setRegisteredRestaurants(response.data);
    } catch (error) {
      console.error('Error fetching registered restaurants:', error);
    }
  };

  return (
    <Box p={4}>
      <Typography variant="h4" align="center" gutterBottom>
        Restaurant Registration
      </Typography>
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Button variant="contained" onClick={createSession}>
            Create Session
          </Button>
        </Grid>
        <Grid item xs={12}>
          <input
            type="file"
            onChange={(e) => setPictureFile(e.target.files[0])}
            style={{ display: 'none' }}
            id="picture-upload"
          />
          <label htmlFor="picture-upload">
            <Button variant="contained" component="span">
              Select Picture
            </Button>
          </label>
          <Button variant="contained" onClick={uploadPicture} disabled={!pictureFile}>
            Upload Picture
          </Button>
        </Grid>
        <Grid item xs={12}>
          <TextField
            label="Restaurant Name"
            fullWidth
            value={restaurantName}
            onChange={(e) => setRestaurantName(e.target.value)}
          />
        </Grid>
        <Grid item xs={12}>
          <TextField
            label="Restaurant Type"
            fullWidth
            value={restaurantType}
            onChange={(e) => setRestaurantType(e.target.value)}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField
            label="Open Time"
            fullWidth
            value={openTime}
            onChange={(e) => setOpenTime(e.target.value)}
          />
        </Grid>
        <Grid item xs={12} sm={6}>
          <TextField
            label="Close Time"
            fullWidth
            value={closeTime}
            onChange={(e) => setCloseTime(e.target.value)}
          />
        </Grid>
        <Grid item xs={12}>
          <Button variant="contained" onClick={registerRestaurant}>
            Register Restaurant
          </Button>
        </Grid>
      </Grid>
      <Box mt={4}>
        <Typography variant="h5" gutterBottom>
          Registered Restaurants
        </Typography>
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Name</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Open Time</TableCell>
                <TableCell>Close Time</TableCell>
              </TableRow>
            </TableHead>
            {/*<TableBody>*/}
            {/*  {registeredRestaurants.map((restaurant) => (*/}
            {/*    <TableRow key={restaurant.id}>*/}
            {/*      <TableCell>{restaurant.name}</TableCell>*/}
            {/*      <TableCell>{restaurant.type}</TableCell>*/}
            {/*      <TableCell>{restaurant.openTime}</TableCell>*/}
            {/*      <TableCell>{restaurant.closeTime}</TableCell>*/}
            {/*    </TableRow>*/}
            {/*  ))}*/}
            {/*</TableBody>*/}
          </Table>
        </TableContainer>
      </Box>
    </Box>
  );
};

export default RestaurantRegistration;
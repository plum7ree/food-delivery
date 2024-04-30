import React, {useState, useEffect} from 'react';
import {useLocation, useNavigate, useParams} from 'react-router-dom';
import axios from 'axios';
import {Box, Grid} from "@mui/material";
import axiosInstance from "../state/axiosInstance";
import {v4 as uuidv4} from 'uuid';

const RestaurantPicture = () => {
   return (
      <Box>

      </Box>
   )
}

const RestaurantMenuLabel = (props) => {
   const navigate = useNavigate();

   const {menu} = props;
   const navigateToMenu = () => {
      console.log(menu)
      navigate(`/eats/restaurant/menu`, {
         state: {
            optionGroups: menu.optionGroupDtoList
         }
      });
   }

   return (
      <Grid container mt={2} spacing={2} onClick={navigateToMenu}>
         <Grid key={uuidv4()} item xs={12} sm={6}>
            {menu.name}
            {/*menu name*/}
         </Grid>
         <Grid key={uuidv4()} item xs={12} sm={6} rowSpan={2}>
            picture
         </Grid>
         <Grid key={uuidv4()} item xs={12} sm={6}>
            {menu.price}
            {/*menu price*/}
         </Grid>
      </Grid>
   )
}

const RestaurantPage = () => {
   const location = useLocation(); // useLocation 훅을 사용하여 URL 정보를 가져옵니다.
   const navigate = useNavigate();
   const [restaurantIdState, setRestaurantIdState] = useState({}); // type 상태를 선언합니다.
   const [restaurantState, setRestaurantState] = useState({});

   useEffect(() => {
      // URL에서 type 파라미터를 가져옵니다.
      const {restaurantId} = location.state;
      setRestaurantIdState(restaurantId)

      const fetchRestaurantContent = async (restaurantId) => {
         try {
            const response = await axiosInstance.get(`/user/api/seller/restaurant/${restaurantId}`)
            setRestaurantState(response.data)
            console.log(response.data)
            // Save the data to a cache file
            // const filePath = path.join(__dirname, 'restaurant_data.json');
            // fs.writeFileSync(filePath, JSON.stringify(response.data));
         } catch (error) {
            console.error('Error fetching restaurant data:', error);

            // Try to read the data from the file if no response
            try {
               const filePath = path.join(__dirname, 'restaurant_data.json');
               const data = fs.readFileSync(filePath, 'utf-8');
               const restaurantData = JSON.parse(data);
               setRestaurantState(restaurantData);
            } catch (fileError) {
               console.error('Error reading restaurant data from file:', fileError);
            }
         }
      }
      fetchRestaurantContent(restaurantId);
   }, [location]); // location.search가 변경될 때마다 useEffect가 실행됩니다.

   // menu 만 별도로 관리하고 싶으면 이렇게.
   // const [menus, setMenus] = useState([]);
   // useEffect(() => {
   //   var restaurant = location.state['restaurant']
   //   setRestaurant(restaurant)
   //   setMenus(restaurant.menus)
   // }, [location]);


   return (
      <Box>
         <RestaurantPicture/>
         {/*restaurant 이 아니라 restaurant.menus 에 걸어도 감지 가능한가?*/}
         {restaurantState && restaurantState.menuDtoList && restaurantState.menuDtoList.map((menu, index) => (
            <RestaurantMenuLabel menu={menu} key={uuidv4()}/>
         ))}

      </Box>
   );
};

export default RestaurantPage;

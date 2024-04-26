import React, { useState, useEffect } from 'react';
import {useLocation, useNavigate, useParams} from 'react-router-dom';
import axios from 'axios';
import {Box} from "@mui/material";

const RestaurantPicture = () => {
  return (
     <Box>


     </Box>
  )
}

const RestaurantMenuLabel = (props) => {
  const {menu} = props;
  return (
    <Grid container spacing={2}>
      <Grid item xs={12} sm={6}>
        {/*{menu.name}*/}
        menu name
      </Grid>
      <Grid item xs={12} sm={6} rowSpan={2}>
        picture
      </Grid>
      <Grid item xs={12} sm={6}>
        {/*{menu.price}*/}
        menu price
      </Grid>
    </Grid>
  )
}

const RestaurantPage = () => {
  const location = useLocation(); // useLocation 훅을 사용하여 URL 정보를 가져옵니다.
  const navigate = useNavigate();
  const [restaurant, setRestaurant] = useState(null); // type 상태를 선언합니다.

  useEffect(() => {
    // URL에서 type 파라미터를 가져옵니다.
    var restaurant = location.state['restaurant']
     console.log(location)
     setRestaurant(restaurant)
  }, [location]); // location.search가 변경될 때마다 useEffect가 실행됩니다.

   // menu 만 별도로 관리하고 싶으면 이렇게.
   // const [menus, setMenus] = useState([]);
   // useEffect(() => {
   //   var restaurant = location.state['restaurant']
   //   setRestaurant(restaurant)
   //   setMenus(restaurant.menus)
   // }, [location]);


   console.log(restaurant)
  return (
     <Box>
       <RestaurantPicture />
        // restaurant 이 아니라 restaurant.menus 에 걸어도 감지 가능한가?
       {restaurant.menus.map((menu, index) => (
          <RestaurantMenuLabel />
       ))}

     </Box>
  );
};

export default RestaurantPage;

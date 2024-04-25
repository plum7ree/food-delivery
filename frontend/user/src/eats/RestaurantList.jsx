import React, { useState, useEffect } from 'react';
import {Link, useLocation, useNavigate} from 'react-router-dom'; // useLocation을 사용하여 URL 쿼리 파라미터를 가져옵니다.
import axiosInstance from "../state/axiosInstance";

const RestaurantList = () => {
  const [restaurants, setRestaurants] = useState([]);
  const [type, setType] = useState(null); // type 상태를 선언합니다.
  const location = useLocation(); // useLocation 훅을 사용하여 URL 정보를 가져옵니다.
  const navigate = useNavigate();

  useEffect(() => {
    // URL에서 type 파라미터를 가져옵니다.
    var typeParam = location.state['type']
    // type 파라미터가 존재하는 경우 해당 값을 상태로 설정합니다.
    if (typeParam) {
      setType(typeParam);
    }
  }, [location]); // location.search가 변경될 때마다 useEffect가 실행됩니다.

  useEffect(() => {
    // type이 존재하는 경우에만 레스토랑 목록을 가져옵니다.
    if (type) {
      const fetchRestaurantsByType = async () => {
        try {
          // 해당 타입의 레스토랑 목록을 가져옵니다.
          const response = await axiosInstance.get(`/user/api/seller/restaurants?type=${type}`);
          console.log(response.data)
          setRestaurants(response.data);
        } catch (error) {
          console.error("Error fetching restaurants:", error);
        }
      };
      fetchRestaurantsByType();
    }
  }, [type]); // type이 변경될 때마다 useEffect가 실행됩니다.


  const handleCategoryClick = (restaurantId) => {
    // 해당 카테고리의 타입을 URL에 포함하여 페이지를 이동합니다.
    navigate(`/eats/restaurants/${restaurantId}`, {
       state: {
          id: `${restaurantId}`
       }
     });
  };


  return (
    <div>
      <h2>{type ? `${type} 레스토랑 목록` : '레스토랑 목록'}</h2>
      <div className="restaurant-list">
        {restaurants.map((restaurant) => (
          <div key={restaurant.id} onClick={() => handleCategoryClick(restaurant.id)}>
            <div className="restaurant-item">
              <img src={restaurant.imageUrl} alt={restaurant.name} />
              <p>{restaurant.name}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default RestaurantList;

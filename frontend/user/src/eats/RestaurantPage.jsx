import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';

const RestaurantPage = () => {
  const { id } = useParams(); // URL에서 레스토랑 ID를 가져옵니다.
  const [restaurant, setRestaurant] = useState(null); // 레스토랑 상세 정보를 담을 상태

  useEffect(() => {
    const fetchRestaurant = async () => {
      try {
        const response = await axios.get(`/api/restaurants/${id}`); // API 경로에 레스토랑 ID를 전달하여 해당 레스토랑 정보를 가져옵니다.
        setRestaurant(response.data); // 가져온 정보를 상태에 저장합니다.
      } catch (error) {
        console.error('Error fetching restaurant:', error);
      }
    };

    fetchRestaurant(); // 함수를 호출하여 레스토랑 정보를 가져옵니다.
  }, [id]);

  if (!restaurant) {
    return <div>Loading...</div>; // 데이터를 가져오는 동안 로딩 상태를 표시합니다.
  }

  return (
    <div>
      <h2>{restaurant.name}</h2>
      <p>Address: {restaurant.address}</p>
      <p>Phone: {restaurant.phone}</p>
      {/* 기타 레스토랑 정보를 표시합니다. */}
    </div>
  );
};

export default RestaurantPage;

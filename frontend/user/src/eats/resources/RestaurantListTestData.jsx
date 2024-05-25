import restaurantData from './web_crawled_restaurant_data.json';


export const GetTestRestaurantListByType = (type) => restaurantData.filter(restaurant => restaurant.type === type);
export const GetGroupedRestaurantsByType = () => {
  return restaurantData.reduce((acc, restaurant) => {
    if (!acc[restaurant.type]) {
      acc[restaurant.type] = [];
    }
    acc[restaurant.type].push(restaurant);
    return acc;
  }, {});
};

export const GetAllTestRestaurantList=restaurantData;

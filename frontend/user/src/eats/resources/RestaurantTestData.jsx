import restaurantData from './web_crawled_restaurant_data.json';

export const RestaurantTestData = restaurantData[0];

export const SearchRestaurantTestData = (id) => {
   return restaurantData.filter((restaurant) => {return restaurant.id === id});
}
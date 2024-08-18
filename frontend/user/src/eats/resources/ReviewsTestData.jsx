import reviewsData from './generated_reviews.json';

const createReviewsMap = () => {
  const reviewsMap = new Map();

  reviewsData.forEach(review => {
    const { restaurantId } = review;
    if (!reviewsMap.has(restaurantId)) {
      reviewsMap.set(restaurantId, []);
    }
    reviewsMap.get(restaurantId).push(review);
  });

  return reviewsMap;
};

const ReviewsTestData = createReviewsMap();

export default ReviewsTestData;
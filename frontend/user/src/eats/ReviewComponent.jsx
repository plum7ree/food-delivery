import React from 'react';
import { Grid, Typography, Rating, LinearProgress, Button, Divider, Avatar } from '@mui/material';

const ReviewSummary = ({ rating, reviewCount, ratingDistribution }) => (
  <Grid container spacing={2} sx={{ backgroundColor: '#f5f5f5', padding: 2, marginBottom: 2 }}>
    <Grid item xs={4}>
      <Typography variant="h3" align="center">{rating.toFixed(1)}</Typography>
      <Rating value={rating} readOnly size="small" sx={{ display: 'flex', justifyContent: 'center' }} />
      <Typography variant="body2" align="center">평가 {reviewCount}개</Typography>
    </Grid>
    <Grid item xs={8}>
      {Object.entries(ratingDistribution).map(([stars, percentage]) => (
        <Grid container alignItems="center" key={stars} spacing={1}>
          <Grid item xs={1}>
            <Typography variant="body2">{stars}</Typography>
          </Grid>
          <Grid item xs={10}>
            <LinearProgress variant="determinate" value={percentage} sx={{ height: 8, borderRadius: 4 }} />
          </Grid>
          <Grid item xs={1}>
            <Typography variant="body2">{percentage}%</Typography>
          </Grid>
        </Grid>
      ))}
    </Grid>
  </Grid>
);

const ReviewItem = ({ review }) => (
  <Grid container spacing={2} sx={{ marginBottom: 2 }}>
    <Grid item xs={12}>
      <Grid container justifyContent="space-between" alignItems="center">
        <Grid item>
          <Typography variant="subtitle1">{review.userName}</Typography>
        </Grid>
        <Grid item>
          <Rating value={review.rating} readOnly size="small" />
        </Grid>
      </Grid>
    </Grid>
    <Grid item xs={12}>
      <Typography variant="body2" color="text.secondary">{review.date}</Typography>
    </Grid>
    {review.imageUrl && (
      <Grid item xs={12}>
        <img src={review.imageUrl} alt="Review" style={{ width: '100%', borderRadius: 8 }} />
      </Grid>
    )}
    <Grid item xs={12}>
      <Typography variant="body1">{review.comment}</Typography>
    </Grid>
    <Grid item xs={12}>
      <Button variant="outlined" size="small">도움이 돼요</Button>
      <Button variant="outlined" size="small" sx={{ marginLeft: 1 }}>도움이 안돼요</Button>
    </Grid>
    <Grid item xs={12}>
      <Divider />
    </Grid>
  </Grid>
);

const ReviewComponent = ({ reviews, rating, reviewCount, ratingDistribution }) => {
  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <ReviewSummary rating={rating} reviewCount={reviewCount} ratingDistribution={ratingDistribution} />
      </Grid>
      <Grid item xs={12}>
        <Typography variant="h6">포토리뷰</Typography>
      </Grid>
      {reviews.map((review, index) => (
        <Grid item xs={12} key={index}>
          <ReviewItem review={review} />
        </Grid>
      ))}
    </Grid>
  );
};

export default ReviewComponent;
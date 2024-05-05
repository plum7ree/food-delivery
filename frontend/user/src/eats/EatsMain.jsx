import React, { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from "react-redux";
import { fetchProfilePicture } from "../state/fetchProfilePicture";
import { Grid, Typography, TextField, IconButton } from '@mui/material';
import { styled } from '@mui/material/styles';
import SearchIcon from '@mui/icons-material/Search';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import {Container} from "@mui/system";

const SearchBar = styled(Grid)(({ theme }) => ({
  display: 'flex',
  alignItems: 'center',
  backgroundColor: theme.palette.background.paper,
  padding: theme.spacing(1),
  borderRadius: theme.shape.borderRadius,
}));

const EatsMain = () => {
  const categories = [
    { name: '피자', icon: '🍕', type: 'pizza' },
    { name: '치킨', icon: '🍗', type: 'chicken' },
    { name: '햄버거', icon: '🍔', type: 'BURGER' },
    { name: '아시안', icon: '🍜', type: 'asian' },
    { name: '멕시코', icon: '🌮', type: 'mexican' },
    { name: '디저트', icon: '🍰', type: 'desert' },
  ];

  const dispatch = useDispatch();
  const profilePictureUrl = useSelector((state) => state.profilePicture.url);
  const navigate = useNavigate();

  useEffect(() => {
    dispatch(fetchProfilePicture());
  }, [dispatch]);

  const handleCategoryClick = (categoryType) => {
    navigate(`/eats/restaurants/${categoryType}`, {
      state: {
        type: `${categoryType}`
      }
    });
  };

  return (
     <Container maxWidth="sm">
    <Grid container direction="column" spacing={2}>
      <Grid item>
        <Grid container alignItems="center" justifyContent="space-between">
          <SearchBar item xs>
            <SearchIcon />
            <TextField placeholder="음식점 또는 음식 검색" fullWidth />
          </SearchBar>
          <IconButton component={Link} to="/eats/mypage">
            {profilePictureUrl ? (
              <img src={profilePictureUrl} alt="Profile" />
            ) : (
              <AccountCircleIcon fontSize="large" />
            )}
          </IconButton>
        </Grid>
      </Grid>

      <Grid item>
        <Typography variant="h6" mb={3}>인기 카테고리</Typography>
        <Grid container spacing={2} mb={3} alignItems="center" justifyContent="space-between" >
          {categories.map((category, index) => (
            <Grid item key={index} onClick={() => handleCategoryClick(category.type)}>
              <Grid container direction="column" alignItems="center">
                <Typography variant="h4">{category.icon}</Typography>
                <Typography>{category.name}</Typography>
              </Grid>
            </Grid>
          ))}
        </Grid>
      </Grid>

      <Grid item>
        <Typography variant="h6">인기 음식점</Typography>
        {/* 인기 음식점 목록 */}
      </Grid>
    </Grid>


     </Container>
  );
};

export default EatsMain;
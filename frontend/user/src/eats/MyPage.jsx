// // MyPage.js
// import React from 'react';
// import {Link} from 'react-router-dom';
// import {Container} from "@mui/system";
// import {Grid, Typography} from "@mui/material";
// import { useTheme } from '@mui/material/styles';
//
// const MyPage = () => {
//    const theme = useTheme();
//
//    return (
//       <Container maxWidth="sm">
//          <Grid item sx={{ backgroundColor: theme.palette.success.main, padding: 2 }}>
//             <Typography variant="h5" color="white">My Page</Typography>
//          </Grid>
//          <Grid container direction="column">
//             <Grid container item ml={3} mt={3}>
//                <Link to="/eats/restaurant-manage" style={{ textDecoration: 'none', color: 'inherit' }}>
//                   <Typography variant="h6" color="success">Register Restaurant</Typography>
//                </Link>
//             </Grid>
//             <Grid container item ml={3} mt={3}>
//                <Link to="/eats/restaurant-manage" style={{ textDecoration: 'none', color: 'inherit' }}>
//                   <Typography variant="h6" color="success">Register Restaurant</Typography>
//                </Link>
//             </Grid>
//             <Grid container item ml={3} mt={3}>
//                <Link to="/eats/restaurant-manage" style={{ textDecoration: 'none', color: 'inherit' }}>
//                   <Typography variant="h6" color="success">Register Restaurant</Typography>
//                </Link>
//             </Grid>
//             <Grid container item ml={3} mt={3}>
//                <Link to="/eats/restaurant-manage" style={{ textDecoration: 'none', color: 'inherit' }}>
//                   <Typography variant="h6" color="success">Register Restaurant</Typography>
//                </Link>
//             </Grid>
//          </Grid>
//       </Container>
//    );
// };
//
//
// export default MyPage;


import React from 'react';
import {Container, Grid, Typography, Box, Avatar, Button, ListItemIcon, IconButton} from '@mui/material';
import HomeIcon from '@mui/icons-material/Home';
import SearchIcon from '@mui/icons-material/Search';
import FavoriteIcon from '@mui/icons-material/Favorite';
import ReceiptIcon from '@mui/icons-material/Receipt';
import PersonIcon from '@mui/icons-material/Person';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import LocalOfferIcon from '@mui/icons-material/LocalOffer';
import EventIcon from '@mui/icons-material/Event';
import GroupAddIcon from '@mui/icons-material/GroupAdd';
import RedeemIcon from '@mui/icons-material/Redeem';
import {Link, useNavigate} from 'react-router-dom';
import {ArrowBack, ChatBubble, Home, Restaurant} from "@mui/icons-material";
import {SiAppwrite} from "react-icons/si";
import {useDispatch} from "react-redux";
import ExitToAppIcon from '@mui/icons-material/ExitToApp';
import {asyncLogout} from "../state/authSlice";


const MyPage = () => {
   const navigate = useNavigate();
   const dispatch = useDispatch();

   const handleLogout = async () => {
      await dispatch(asyncLogout());
      navigate('/login');
   };
   return (
      <Container maxWidth="sm">
         <Grid container item justifyContent="space-between">
            <Grid item>
               <IconButton onClick={() => {
                  navigate(-1)
               }}>
                  <ArrowBack/>
               </IconButton>
            </Grid>
            <Grid item>
               <IconButton>
                  <Home/>
               </IconButton>
            </Grid>
         </Grid>
         <Grid container direction="column" alignItems="center" spacing={2} sx={{padding: 2}}>
            <Grid container item direction="row">
               <Grid item>
                  <Avatar
                     alt="Profile"
                     src="/path-to-your-profile-image.jpg" // 이미지 경로를 여기에 설정하세요.
                     sx={{width: 80, height: 80}}
                  />
               </Grid>
               <Grid item ml={3} mt={2}>
                  <Grid item>
                     <Typography variant="body1">마스터 님</Typography>
                  </Grid>
                  <Grid item>
                     <Typography variant="body1">010-****-****</Typography>
                  </Grid>
               </Grid>
            </Grid>

            <Grid item container justifyContent="space-between" mt={3}>
               <Box textAlign="center">
                  <ReceiptIcon/>
                  <Typography variant="h6">0</Typography>
                  <Typography variant="body2">주문내역</Typography>
               </Box>
               <Box textAlign="center">
                  <ChatBubble/>
                  <Typography variant="h6">0</Typography>
                  <Typography variant="body2">내가 남긴 리뷰</Typography>
               </Box>
               <Box textAlign="center">
                  <FavoriteIcon/>
                  <Typography variant="h6">10</Typography>
                  <Typography variant="body2">즐겨찾기</Typography>
               </Box>
            </Grid>

            <Grid item container direction="column" spacing={2} mt={3}>
               <Grid item container alignItems="center" mb={1} mt={1}>
                  <Restaurant/>
                  <Link to="/eats/restaurant-manage" style={{textDecoration: 'none', color: 'inherit'}}>
                     <Typography variant="body1" ml={1}>레스토랑 관리</Typography>

                  </Link>
               </Grid>
               <Grid item container alignItems="center" mb={1} mt={1}>
                  <LocationOnIcon/>
                  <Typography variant="body1" ml={1}>주소 관리</Typography>
               </Grid>
               <Grid item container alignItems="center" mb={1} mt={1}>
                  <LocalOfferIcon/>
                  <Typography variant="body1" ml={1}>할인쿠폰</Typography>
               </Grid>
               <Grid item container alignItems="center" mb={1} mt={1}>
                  <EventIcon/>
                  <Typography variant="body1" ml={1}>진행중인 이벤트</Typography>
               </Grid>
               <Grid item container alignItems="center" mb={1} mt={1}>
                  <GroupAddIcon/>
                  <Typography variant="body1" ml={1}>친구 초대</Typography>
               </Grid>
               <Grid item container alignItems="center" mb={1} mt={1}>
                  <ExitToAppIcon/>
                  <Button
                     onClick={handleLogout}
                     style={{textDecoration: 'none', color: 'inherit'}}
                  >
                     <Typography variant="body1" ml={1}>로그아웃</Typography>
                  </Button>
               </Grid>
            </Grid>
         </Grid>
      </Container>
   );
};

export default MyPage;

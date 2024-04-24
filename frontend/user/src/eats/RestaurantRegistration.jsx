import React, {useState, useEffect} from 'react';
import axios from 'axios';
import {
   Box,
   Typography,
   Button,
   TextField,
   Grid,
   Paper,
   Table,
   TableBody,
   TableCell,
   TableContainer,
   TableHead,
   TableRow,
   MenuItem,
   Collapse,
} from '@mui/material';
import {ExpandMore, ExpandLess} from '@mui/icons-material';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';

import {Container, styled} from '@mui/system';

import {useSelector} from 'react-redux';

const StyledButton = styled(Button)(({theme}) => ({
   backgroundColor: 'white',
     color: 'black', // textColor 수정
   width: '100%',
   borderRadius: '0',
   justifyContent: 'space-between',
   '&:hover': {
      backgroundColor: 'white',
   },
}));
const ExpandedContent = styled(Box)({
   width: '100%',
   backgroundColor: 'white',
   padding: theme => theme.spacing(2),
});


const RestaurantRegistration = () => {
   const [sessionId, setSessionId] = useState('');
   const [restaurantName, setRestaurantName] = useState('');
   const [restaurantType, setRestaurantType] = useState('');
   const [openTime, setOpenTime] = useState('');
   const [closeTime, setCloseTime] = useState('');
   const [pictureFile, setPictureFile] = useState(null);
   const [registeredRestaurants, setRegisteredRestaurants] = useState([]);
   const [menus, setMenus] = useState([
      {
         name: '', description: '', price: '',
         optionGroups: null
      },
   ]);
   const axiosInstance = useSelector((state) => state.axiosInstance.instance);

   useEffect(() => {
      fetchRegisteredRestaurants();
   }, []);


   const createSession = async () => {
      try {
         const response = await axiosInstance.post('/user/api/seller/create-session');
         setSessionId(response.data);
      } catch (error) {
         console.error('Error creating session:', error);
      }
   };

   const uploadPicture = async () => {
      try {
         const formData = new FormData();
         formData.append('file', pictureFile);
         formData.append('sessionId', sessionId);
         await axiosInstance.post('/user/api/seller/restaurant-picture-resized', formData);
      } catch (error) {
         console.error('Error uploading picture:', error);
      }
   };

   const registerRestaurant = async () => {
      try {
         const restaurantData = {
            name: restaurantName,
            sessionId: sessionId,
            type: restaurantType,
            openTime: openTime,
            closeTime: closeTime,
            menuDtoList: menus,
         };
         await axiosInstance.post(`/user/api/seller/register/restaurant`, restaurantData);
         fetchRegisteredRestaurants();
      } catch (error) {
         console.error('Error registering restaurant:', error);
      }
   };

   const fetchRegisteredRestaurants = async () => {
      try {
         const response = await axiosInstance.get('/user/api/seller/registered-restaurant');
         setRegisteredRestaurants(response.data);
      } catch (error) {
         console.error('Error fetching registered restaurants:', error);
      }
   };

   const addMenu = () => {
      setMenus([...menus, {name: '', description: '', price: '', optionGroups: null}]);
   };

   const handleMenuChange = (index, field, value) => {
      const updatedMenus = [...menus];
      updatedMenus[index][field] = value;
      setMenus(updatedMenus);
   };
   const addOptionGroup = (menuIndex) => {
      const updatedMenus = [...menus];
      if(!updatedMenus[menuIndex].optionGroups) {
         updatedMenus[menuIndex].optionGroups = []
      }
      updatedMenus[menuIndex].optionGroups.push({
         name : '',
         isNecessary: false,
         isDuplicatedAllowed: true,
         options: null
      });
      setMenus(updatedMenus);
   };

   const addOption = (menuIndex, optionGroupIndex) => {
      const updatedMenus = [...menus];
      if(!updatedMenus[menuIndex].optionGroups[optionGroupIndex].options) {
         updatedMenus[menuIndex].optionGroups[optionGroupIndex].options = []
      }
      updatedMenus[menuIndex].optionGroups[optionGroupIndex].options.push({name: '', cost: ''});
      setMenus(updatedMenus);
   };

   const handleOptionGroupChange = (menuIndex, optionGroupIndex, field, value) => {
      const updatedMenus = [...menus];
      updatedMenus[menuIndex].optionGroups[optionGroupIndex][field] = value;
      setMenus(updatedMenus);
   };

   const handleOptionChange = (menuIndex, optionGroupIndex, optionIndex, field, value) => {
      const updatedMenus = [...menus];
      updatedMenus[menuIndex].optionGroups[optionGroupIndex].options[optionIndex][field] = value;
      setMenus(updatedMenus);
   };

   // 메뉴 리스트 펼침/접기 상태 관리
   const [expandedMenus, setExpandedMenus] = useState([]);

   // 메뉴 리스트 항목 토글 함수
   const toggleMenuExpansion = (index) => {
      setExpandedMenus((prevExpanded) => {
         const newExpanded = [...prevExpanded];
         if (newExpanded.includes(index)) {
            newExpanded.splice(newExpanded.indexOf(index), 1);
         } else {
            newExpanded.push(index);
         }
         return newExpanded;
      });
   };

   return (
      <Box p={4}>
         <Typography variant="h4" align="left" gutterBottom>
            Restaurant Registration
         </Typography>
         <Grid container spacing={2} alignItems="flex-start">
            <Grid item xs={12}>
               <Button variant="contained" style={{float: 'left'}} onClick={createSession}>
                  Create Session
               </Button>
            </Grid>
            <Grid item xs={12}>
               <input
                  type="file"
                  onChange={(e) => setPictureFile(e.target.files[0])}
                  style={{display: 'none'}}
                  id="picture-upload"
               />
               <label htmlFor="picture-upload">
                  <Button variant="contained" style={{float: 'left'}} component="span">
                     Select Picture
                  </Button>
               </label>
               <Button variant="contained" style={{float: 'left'}} onClick={uploadPicture} disabled={!pictureFile}>
                  Upload Picture
               </Button>
            </Grid>
            <Grid item xs={12}>
               <TextField
                  label="Restaurant Name"
                  fullWidth
                  value={restaurantName}
                  onChange={(e) => setRestaurantName(e.target.value)}
               />
            </Grid>
            <Grid item xs={12}>
               <TextField
                  label="Restaurant Type"
                  fullWidth
                  value={restaurantType}
                  onChange={(e) => setRestaurantType(e.target.value)}
                  select
               >
                  <MenuItem value="BURGER">Burger</MenuItem>
                  <MenuItem value="PIZZA">Pizza</MenuItem>
                  <MenuItem value="KOREAN">Korean</MenuItem>
                  <MenuItem value="CHINESE">Chinese</MenuItem>
                  <MenuItem value="JAPANESE">Japanese</MenuItem>
                  <MenuItem value="MEXICAN">Mexican</MenuItem>
                  <MenuItem value="ITALIAN">Italian</MenuItem>
                  <MenuItem value="AMERICAN">American</MenuItem>
                  <MenuItem value="FUSION">Fusion</MenuItem>
                  <MenuItem value="MISC">Misc</MenuItem>
               </TextField>
            </Grid>
            <Grid item xs={12} sm={6}>
               <TextField
                  label="Open Time"
                  fullWidth
                  type="time"
                  value={openTime}
                  onChange={(e) => setOpenTime(e.target.value)}
                  InputLabelProps={{
                     shrink: true,
                  }}
                  inputProps={{
                     step: 300, // 5분 단위로 선택 가능
                  }}
               />
            </Grid>
            <Grid item xs={12} sm={6}>
               <TextField
                  label="Close Time"
                  fullWidth
                  type="time"
                  value={closeTime}
                  onChange={(e) => setCloseTime(e.target.value)}
                  InputLabelProps={{
                     shrink: true,
                  }}
                  inputProps={{
                     step: 300, // 5분 단위로 선택 가능
                  }}
               />
            </Grid>
            <Grid item xs={12} mt={2} ml={4}>
               <Typography align="left">Menus</Typography>
               {menus.map((menu, menuIndex) => (
                  <Box key={menuIndex}>
                     <StyledButton
                        variant="contained"
                        onClick={() => toggleMenuExpansion(menuIndex)}
                        endIcon={expandedMenus.includes(menuIndex) ? <ExpandLess/> : <ExpandMore/>}

                     >
                        {menu.name}
                     </StyledButton>
                     <Collapse in={expandedMenus.includes(menuIndex)} timeout="auto" unmountOnExit>
                        <ExpandedContent>

                           <TextField
                              label="Menu Name"
                              fullWidth
                              value={menu.name}
                              onChange={(e) => handleMenuChange(menuIndex, 'name', e.target.value)}
                           />
                           <TextField
                              label="Menu Description"
                              fullWidth
                              value={menu.description}
                              onChange={(e) => handleMenuChange(menuIndex, 'description', e.target.value)}
                           />
                           <TextField
                              label="Menu Price"
                              fullWidth
                              value={menu.price}
                              onChange={(e) => handleMenuChange(menuIndex, 'price', e.target.value)}
                           />

                           {/* Option Group 생성. */}
                           {/* 1. button */}
                           <Button variant="contained" style={{float: "left"}}
                                   onClick={() => addOptionGroup(menuIndex)}>
                              + Add Option Group
                           </Button>
                           {/* 2. input fields */}
                           <Container style={{float: "left"}}>
                              {menu.optionGroups && menu.optionGroups.map((optionGroup, optionGroupIndex) => (
                                 <Box key={optionGroupIndex} ml={4}>
                                    <Typography variant="subtitle2" align="left" mt={2}>Option</Typography>
                                    <TextField
                                       label="name"
                                       fullWidth
                                       value={optionGroup.name}
                                       onChange={(e) =>
                                          handleOptionGroupChange(menuIndex, optionGroupIndex, 'name', e.target.value)
                                       }
                                    />
                                    <TextField
                                       label="Is Duplicated Allowed"
                                       fullWidth
                                       select
                                       value={optionGroup.isDuplicatedAllowed}
                                       onChange={(e) =>
                                          handleOptionGroupChange(menuIndex, optionGroupIndex, 'isDuplicatedAllowed', e.target.value)
                                       }
                                    >
                                       <MenuItem value="true">True</MenuItem>
                                       <MenuItem value="false">False</MenuItem>
                                    </TextField>
                                    <TextField
                                       label="Is Necessary"
                                       fullWidth
                                       select
                                       value={optionGroup.isNecessary}
                                       onChange={(e) =>
                                          handleOptionGroupChange(menuIndex, optionGroupIndex, 'isNecessary', e.target.value)
                                       }
                                    >
                                       <MenuItem value="true">True</MenuItem>
                                       <MenuItem value="false">False</MenuItem>
                                    </TextField>

                                       {/*Option  생성 */}
                                       {/* 1.button*/}
                                       <Button variant="contained" style={{float: "left"}}
                                               onClick={() => addOption(menuIndex, optionGroupIndex)}>
                                          + Add Option
                                       </Button>
                                       {/* 2. input field */}
                                       <Container style={{float:"left"}}>
                                       {optionGroup.options && optionGroup.options.map((option, optionIndex) => (
                                          <Box key={optionIndex} ml={4}>
                                             <Typography variant="subtitle2" align="left" mt={2}>Select Field</Typography>
                                             <TextField
                                                label="Option Name"
                                                fullWidth
                                                value={option.name}
                                                onChange={(e) =>
                                                   handleOptionChange(menuIndex, optionGroupIndex, optionIndex, 'name', e.target.value)
                                                }
                                             />
                                             <TextField
                                                label="Option Cost"
                                                fullWidth
                                                value={option.cost}
                                                onChange={(e) =>
                                                   handleOptionChange(menuIndex, optionGroupIndex, optionIndex, 'cost', e.target.value)
                                                }
                                             />
                                          </Box>
                                       ))}
                                       </Container>
                                 </Box>
                              ))}
                           </Container>

                        </ExpandedContent>
                     </Collapse>
                  </Box>
               ))}
               <Button variant="contained" style={{float: "left"}} onClick={addMenu}>
                  + Add Menu
               </Button>
            </Grid>
            <Grid item xs={12}>
               <Button variant="contained" onClick={registerRestaurant}>
                  Register Restaurant
               </Button>
            </Grid>
         </Grid>
         <Box mt={4}>
            <Typography >
               Registered Restaurants
            </Typography>
            <TableContainer component={Paper}>
               <Table>
                  <TableHead>
                     <TableRow>
                        <TableCell>Name</TableCell>
                        <TableCell>Type</TableCell>
                        <TableCell>Open Time</TableCell>
                        <TableCell>Close Time</TableCell>
                     </TableRow>
                  </TableHead>
                  <TableBody>
                     {registeredRestaurants.map((restaurant) => (
                        <TableRow key={restaurant.id}>
                           <TableCell>{restaurant.name}</TableCell>
                           <TableCell>{restaurant.type}</TableCell>
                           <TableCell>{restaurant.openTime}</TableCell>
                           <TableCell>{restaurant.closeTime}</TableCell>
                        </TableRow>
                     ))}
                  </TableBody>
               </Table>
            </TableContainer>
         </Box>
      </Box>
   )
      ;
};

export default RestaurantRegistration;
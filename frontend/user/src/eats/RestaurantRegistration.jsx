import React, {useEffect, useState} from 'react';
import {
   Box,
   Button,
   Collapse,
   Grid,
   MenuItem,
   Paper,
   Table,
   TableBody,
   TableCell,
   TableContainer,
   TableHead,
   TableRow,
   TextField,
   Typography,
} from '@mui/material';
import {ExpandLess, ExpandMore} from '@mui/icons-material';
import DeleteIcon from '@mui/icons-material/Delete';

import {Container, styled} from '@mui/system';
import axiosInstance from "../state/axiosInstance";
import RestaurantType from "./RestaurantType";

const StyledButton = styled(Button)(({theme}) => ({
   backgroundColor: 'white',
   color: 'black',
   width: '100%',
   borderRadius: '0',
   justifyContent: 'space-between',
   '&:hover': {
      backgroundColor: 'white',
   },
   '& .MuiSvgIcon-root': {
      fontSize: '1.5rem', // 아이콘 크기 조절
   },
}));
const ExpandedContent = styled(Box)({
   width: '100%',
   backgroundColor: 'white',
   padding: theme => theme.spacing(2),
});


const OptionGroupInputField = (props) => {
   var optionGroup = props.optionGroup;
   var menuIndex = props.menuIndex;
   var optionGroupIndex = props.optionGroupIndex;
   var handleOptionGroupChange = props.handleOptionGroupChange;
   var removeOptionGroup = props.removeOptionGroup;
   return (
      <Grid container mt={3}>
         <Grid container>
            <Grid item xs={6} align="left">
               <Typography variant="subtitle1">Option</Typography>

            </Grid>
            <Grid item align="right" xs={6}>
               <DeleteIcon onClick={() => removeOptionGroup(menuIndex, optionGroupIndex)}/>

            </Grid>
         </Grid>
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
      </Grid>


   )

}


const OptionInputField = (props) => {
   var option = props.option;
   var optionIndex = props.optionIndex;
   var menuIndex = props.menuIndex;
   var optionGroupIndex = props.optionGroupIndex;
   var handleOptionChange = props.handleOptionChange;
   var removeOption = props.removeOption;

   return (
      <Grid container key={optionIndex} ml={4}>
         <Grid container>
            <Grid item xs={6} align="left">
               <Typography variant="subtitle2" align="left" mt={2}>Select Field</Typography>

            </Grid>
            <Grid item align="right" xs={6}>
               <DeleteIcon onClick={() => removeOption(menuIndex, optionGroupIndex, optionIndex)}/>

            </Grid>
         </Grid>
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

      </Grid>
   )

}

const RestaurantRegistration = () => {
   const [restaurantName, setRestaurantName] = useState('');
   const [restaurantType, setRestaurantType] = useState('');
   const [openTime, setOpenTime] = useState('');
   const [closeTime, setCloseTime] = useState('');
   const [pictureFile, setPictureFile] = useState(null);
   const [menuPictureFiles, setMenuPictureFiles] = useState([]);

   const [registeredRestaurants, setRegisteredRestaurants] = useState([]);
   const [menus, setMenus] = useState([
      {
         name: '', description: '', price: '',
         optionGroups: null,
      },
   ]);
   useEffect(() => {
      fetchRegisteredRestaurants();
   }, []);


   const createSession = async () => {
      try {
         return axiosInstance.post('/user/api/seller/create-session').then(response => response.data);

      } catch (error) {
         console.error('Error creating session:', error);
      }
   };

   const uploadRestaurantPicture = async (sessionId) => {
      try {
         if (pictureFile) {
            console.log('trying to upload restaurant picture resized. sessionId: {}', sessionId)
            const formData = new FormData();
            formData.append('file', pictureFile);
            formData.append('sessionId', sessionId);
            formData.append('type', 'restaurant');
            axiosInstance.post('/user/api/seller/register/picture', formData);

         } else {
            console.error("no restaurant picture pictureFile: ", pictureFile)
         }
      } catch (error) {
         console.error('Error uploading picture:', error);
      }
   };

   const uploadMenuPicture = async (sessionId) => {
      try {
         if (menuPictureFiles) {
            menuPictureFiles.map((file) => {
               const formData = new FormData();
               formData.append('file', pictureFile);
               formData.append('sessionId', sessionId);
               formData.append('type', 'menu');
               axiosInstance.post('/user/api/seller/register/picture', formData);
            })
         } else {
            console.error("no restaurant picture pictureFile: ", pictureFile)
         }
      } catch (error) {
         console.error('Error uploading picture:', error);
      }
   };


   const registerRestaurant = async () => {
      try {
         const sessionId = await createSession()
         console.log("sessionId created : " + sessionId)
         const restaurantData = {
            name: restaurantName,
            sessionId: sessionId,
            type: restaurantType,
            openTime: openTime,
            closeTime: closeTime,
            menuDtoList: menus,
         };

         console.log(restaurantData)
         await uploadRestaurantPicture(sessionId);
         await uploadMenuPicture(sessionId);
         await axiosInstance.post(`/user/api/seller/register/restaurant`, restaurantData);
         await fetchRegisteredRestaurants();
      } catch (error) {
         console.error('Error registering restaurant:', error);
      }
   };

   const fetchRegisteredRestaurants = async () => {
      try {
         const response = await axiosInstance.get('/user/api/seller/user-registered-restaurant');
         setRegisteredRestaurants(response.data);
      } catch (error) {
         console.error('Error fetching registered restaurants:', error);
      }
   };

   const addMenu = () => {
      setMenus([...menus, {name: '', description: '', price: '', optionGroups: null}]);
   };
   const deleteMenu = (index) => {
      const updatedMenus = [...menus];
      updatedMenus.splice(index, 1);
      setMenus(updatedMenus);
   };

   const handleMenuChange = (index, field, value) => {
      const updatedMenus = [...menus];
      updatedMenus[index][field] = value;
      setMenus(updatedMenus);
   };
   const addOptionGroup = (menuIndex) => {
      const updatedMenus = [...menus];
      if (!updatedMenus[menuIndex].optionGroups) {
         updatedMenus[menuIndex].optionGroups = []
      }
      updatedMenus[menuIndex].optionGroups.push({
         name: '',
         isNecessary: false,
         isDuplicatedAllowed: true,
         options: null
      });
      setMenus(updatedMenus);
   };
   const removeOptionGroup = (menuIndex, optionGroupIndex) => {
      const updatedMenus = [...menus];
      console.log(updatedMenus);
      updatedMenus[menuIndex].optionGroups.splice(optionGroupIndex, 1);
      setMenus(updatedMenus);
   }

   const removeOption = (menuIndex, optionGroupIndex, optionIndex) => {
      const updatedMenus = [...menus];
      console.log(updatedMenus);
      updatedMenus[menuIndex].optionGroups[optionGroupIndex].options.splice(optionIndex, 1);
      setMenus(updatedMenus);
   }

   const addOption = (menuIndex, optionGroupIndex) => {
      const updatedMenus = [...menus];
      if (!updatedMenus[menuIndex].optionGroups[optionGroupIndex].options) {
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
      <Grid container item lg={6}>
         <Grid item>
            <Typography variant="h4" align="left" gutterBottom>
               Restaurant Registration
            </Typography>
         </Grid>
         <Grid container alignItems="flex-start">
            <Grid container columnSpacing={2}>
               <Grid item>
                  <input
                     type="file"
                     onChange={(e) => setPictureFile(e.target.files[0])}
                     style={{display: 'none'}}
                     id="picture-upload"
                  />
                  <label htmlFor="picture-upload">
                     <Button variant="outlined" style={{float: 'left'}} component="span">
                        Select Picture
                     </Button>
                  </label>
               </Grid>
               <Grid item>
                  <Typography>{pictureFile && pictureFile.name}</Typography>
               </Grid>
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
                  <MenuItem value={RestaurantType.BURGER}>Burger</MenuItem>
                  <MenuItem value={RestaurantType.PIZZA}>Pizza</MenuItem>
                  <MenuItem value={RestaurantType.KOREAN}>Korean</MenuItem>
                  <MenuItem value={RestaurantType.CHINESE}>Chinese</MenuItem>
                  <MenuItem value={RestaurantType.JAPANESE}>Japanese</MenuItem>
                  <MenuItem value={RestaurantType.MEXICAN}>Mexican</MenuItem>
                  <MenuItem value={RestaurantType.ITALIAN}>Italian</MenuItem>
                  <MenuItem value={RestaurantType.AMERICAN}>American</MenuItem>
                  <MenuItem value={RestaurantType.FUSION}>Fusion</MenuItem>
                  <MenuItem value={RestaurantType.MISC}>Misc</MenuItem>
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
            <Grid item xs={12} mt={3} ml={3}>
               <Typography align="left">Menus</Typography>
               {menus.map((menu, menuIndex) => (
                  <Box key={menuIndex} mb={3}>

                     <StyledButton
                        variant="contained"
                        onClick={() => toggleMenuExpansion(menuIndex)}
                        endIcon={expandedMenus.includes(menuIndex) ? (
                           <Box display="flex" alignItems="right">
                              <ExpandLess/>
                              <DeleteIcon onClick={() => deleteMenu(menuIndex)}/>
                           </Box>
                        ) : (
                           <Box display="flex" alignItems="right">
                              <ExpandMore/>
                              <DeleteIcon onClick={() => deleteMenu(menuIndex)}/>
                           </Box>
                        )}

                     >
                        {menu.name}
                     </StyledButton>
                     <Collapse in={expandedMenus.includes(menuIndex)} timeout="auto" unmountOnExit>
                        <ExpandedContent ml={3}>
                           <Grid container alignItems="flex-start">
                              <Grid container columnSpacing={2}>
                                 <Grid item>
                                    <input
                                       type="file"
                                       onChange={(e) => {
                                          const updated = [...menuPictureFiles]
                                          updated[menuIndex] = e.target.files[0];
                                          setMenuPictureFiles(updated)
                                       }
                                       }
                                       style={{display: 'none'}}
                                       id={`menu-picture-upload-${menuIndex}`}
                                    />
                                    <label htmlFor={`menu-picture-upload-${menuIndex}`}>
                                       <Button variant="outlined" style={{float: 'left'}}
                                               component="span">
                                          Select Menu Picture
                                       </Button>
                                    </label>
                                 </Grid>
                                 <Grid item>
                                    <Typography>{menuPictureFiles && menuPictureFiles[menuIndex] && menuPictureFiles[menuIndex].name}</Typography>
                                 </Grid>
                              </Grid>
                           </Grid>
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
                           <Box mb={3}>
                              <Button variant="contained" style={{float: "left"}}
                                      onClick={() => addOptionGroup(menuIndex)}>
                                 + Add Option
                              </Button>

                              {/* 2. input fields */}
                              <Container style={{float: "left"}}>
                                 {menu.optionGroups && menu.optionGroups.map((optionGroup, optionGroupIndex) => (
                                    <Box key={optionGroupIndex} ml={4}>
                                       <OptionGroupInputField optionGroup={optionGroup} menuIndex={menuIndex}
                                                              optionGroupIndex={optionGroupIndex}
                                                              handleOptionGroupChange={handleOptionGroupChange}
                                                              removeOptionGroup={removeOptionGroup}
                                       />

                                       {/*Option  생성 */}
                                       {/* 1.button*/}
                                       <Button variant="contained" style={{float: "left"}}
                                               onClick={() => addOption(menuIndex, optionGroupIndex)}>
                                          + Add Select Field
                                       </Button>

                                       {/* 2. input field */}
                                       <Container style={{float: "left"}}>
                                          {optionGroup.options && optionGroup.options.map((option, optionIndex) => (
                                             (
                                                <OptionInputField key={optionIndex} option={option}
                                                                  optionIndex={optionIndex} menuIndex={menuIndex}
                                                                  optionGroupIndex={optionGroupIndex}
                                                                  handleOptionChange={handleOptionChange}
                                                                  removeOption={removeOption}/>

                                             )
                                          ))}
                                       </Container>
                                    </Box>
                                 ))}
                              </Container>

                           </Box>

                        </ExpandedContent>
                     </Collapse>
                  </Box>
               ))}
               <Button variant="contained" style={{float: "left"}} onClick={addMenu}>
                  + Add Menu
               </Button>
            </Grid>
            <Grid item>
               <Button variant="contained" onClick={registerRestaurant}>
                  Register Restaurant
               </Button>
            </Grid>
         </Grid>
         <Grid item xs={12} mt={10}>
            <Typography>
               Registered Restaurants
            </Typography>
            <TableContainer component={Paper}>
               <Table>
                  <TableHead>
                     <TableRow key='tableheadkey'>
                        <TableCell>Name</TableCell>
                        <TableCell>Type</TableCell>
                        <TableCell>Open Time</TableCell>
                        <TableCell>Close Time</TableCell>
                     </TableRow>
                  </TableHead>
                  <TableBody key='tabkebodykey'>
                     {registeredRestaurants.map((restaurant, index) => (
                        <TableRow key={`restaurant-${index}`}>
                           <TableCell key={`restaurant-name-${index}`}>{restaurant.name}</TableCell>
                           <TableCell key={`restaurant-type-${index}`}>{restaurant.type}</TableCell>
                           <TableCell key={`restaurant-opentime-${index}`}>{restaurant.openTime}</TableCell>
                           <TableCell key={`restaurant-closetime-${index}`}>{restaurant.closeTime}</TableCell>
                        </TableRow>
                     ))}
                  </TableBody>
               </Table>
            </TableContainer>
         </Grid>
      </Grid>
   )
      ;
};

export default RestaurantRegistration;
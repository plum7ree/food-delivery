import React, {useEffect, useState} from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import {Box, Button, Checkbox, FormControl, FormGroup, Grid, IconButton, Typography} from "@mui/material";
import {v4 as uuidv4} from 'uuid';
import {Container} from "@mui/system";
import {useDispatch} from "react-redux";
import {addMenu} from "../state/checkout/selectedMenuSlice";
import {fetchProfilePicture} from "../state/fetchProfilePicture";
import {ArrowBack, Home} from "@mui/icons-material";

const MenuPicture = (props) => {
   const {pictureUrl} = props;
   const navigate = useNavigate();
   return (
            <Grid container item>
         <Grid container item justifyContent="space-between">
            <Grid item>
               <IconButton onClick={() => {navigate(-1)}}>
               <ArrowBack />
             </IconButton>
            </Grid>
            <Grid item>
               <IconButton>
                  <Home/>
               </IconButton>
            </Grid>
         </Grid>

         <Grid container item>
            <img src={pictureUrl} style={{width: '100%', height: 'auto'}}/>

         </Grid>

      </Grid>
   )
}

const OptionLabel = (props) => {
   const {optionGroup} = props;

   return (
      <Grid container>
         <Grid container  justifyContent="space-between">
            <Grid item>
               <Typography variant="h6">{optionGroup.description}</Typography>
            </Grid>
            <Grid item>
               {optionGroup.necessary && (
                  // Grid container 내부에 Grid 는 모두 한 행의 각 열로 순서대로 배치된다.
                  // Grid container 가 없으면 각 열로 배치됨.
                  <Grid container item alignItems="center">
                     <Grid item>
                        <Typography variant="body1" ml={2}>
                           Must Select
                        </Typography>
                     </Grid>
                     <Grid item>
                        <Typography variant="body1" ml={2}>
                           {optionGroup.maxSelectNumber}
                        </Typography>
                     </Grid>
                  </Grid>
               )}
            </Grid>
         </Grid>
      </Grid>
   );
};
const CustomFormControlLabel = (props) => {
   const {option, optionGroupIndex, optionIndex, isChecked, onOptionChange} = props;

   return (
      <Box
         display="flex"
         alignItems="center"
         justifyContent="space-between"
         width="100%"
         sx={{
            border: '1px solid #ddd',
            borderRadius: '4px',
            padding: '8px 12px',
         }}
      >
         <Box display="flex" alignItems="center">
            <Checkbox checked={isChecked} onChange={() => onOptionChange(optionGroupIndex, optionIndex)}/>
            <Typography variant="body2" ml={2}>
               {option.name}
            </Typography>
         </Box>
         <Typography variant="body2">{option.cost}</Typography>
      </Box>
   );
};

const MenuPage = () => {
   const location = useLocation();
   const navigate = useNavigate();
   const {menu, onOptionSelect} = location.state;
   console.log(location.state)
   const optionGroups = menu.optionGroupDtoList;
   /**
    *
    * selectedOptions = {
    *    optionGroupIndex0: {
    *       optionIndex0: true
    *    },
    *
    *    optionGroupIndex1: {
    *       optionIndex0: true
    *    }
    * }
    * example) { 0: { 0 : true }, 1: { 0 : true, 1: true}}
    *
    */
   const [selectedOptionsState, setSelectedOptionsState] = useState({});
   const [thisMenuPriceWithAllOptions, setThisMenuPriceWithAllOptions] = useState(0);

   useEffect(() => {
      // Redux store에서 프로필 사진을 가져오는 액션을 디스패치합니다.
      updateThisMenuPriceWithAllOptions([], {});
   }, []);

   useEffect(() => {
       // 옵션 선택에 따른 총 금액 업데이트
      updateThisMenuPriceWithAllOptions(optionGroups, selectedOptionsState);

   }, [selectedOptionsState]);

   const handleOptionChange = (optionGroupIndex, optionIndex) => {
      setSelectedOptionsState((prevSelectedOptions) => {
         const updatedOptions = {...prevSelectedOptions};

         const currentOptionGroup = updatedOptions[optionGroupIndex] || {};

         if (currentOptionGroup[optionIndex]) {
            /**
             * Instead of False, delete Key.
             * CheckBox's onChange callback can only know that it is changed.
             * It doesn't know it is checked or not.
             */
            delete currentOptionGroup[optionIndex];
         } else {
            currentOptionGroup[optionIndex] = true;
         }

         updatedOptions[optionGroupIndex] = currentOptionGroup;
         return updatedOptions;
      });

     };

   const updateThisMenuPriceWithAllOptions = (optionGroups, selectedOptions) => {
      console.log(optionGroups);
      console.log(selectedOptions)
      let newTotal = menu.price;
      optionGroups.forEach((optionGroup, optionGroupIndex) => {
         optionGroup.optionDtoList.forEach((option, optionIndex) => {
            if (selectedOptions[optionGroupIndex] && selectedOptions[optionGroupIndex][optionIndex]) {
               newTotal += parseInt(option.cost);
            }
         });
      });
      setThisMenuPriceWithAllOptions(newTotal);
   };
   

  const dispatch = useDispatch();
  const handleAddToCart = () => {
    dispatch(addMenu({ menuItem: menu, selectedOptions: selectedOptionsState }));
    navigate(-1);
  };
   return (
      <Container maxWidth="sm">
         <Grid container direction="column" spacing={4} justifyItems="center">
            <Grid container item>
               <MenuPicture pictureUrl={menu.pictureUrl} />

            </Grid>

            {optionGroups && optionGroups.map((optionGroup, optionGroupIndex) => (
               <Grid key={uuidv4()} container item >
                  <FormControl key={uuidv4()} fullWidth={true}>
                     <OptionLabel key={uuidv4()} optionGroup={optionGroup}/>
                     <FormGroup>
                        {optionGroup.optionDtoList.map((option, optionIndex) => (
                           <CustomFormControlLabel
                              key={uuidv4()}
                              option={option}
                              isChecked={selectedOptionsState && selectedOptionsState[optionGroupIndex] && selectedOptionsState[optionGroupIndex][optionIndex] === true}
                              optionGroupIndex={optionGroupIndex}
                              optionIndex={optionIndex}
                              onOptionChange={handleOptionChange}
                           />
                        ))}
                     </FormGroup>
                  </FormControl>
               </Grid>
            ))}
         </Grid>

         <Grid container item justify="center">
            <Button variant="contained"
                    color="success"
                    fullWidth
                    onClick={handleAddToCart}>
               Add to Cart ({thisMenuPriceWithAllOptions}원)
            </Button>
         </Grid>
      </Container>
   );
};

export default MenuPage;

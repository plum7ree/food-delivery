import React, {useState, useEffect} from 'react';
import {useLocation, useParams} from 'react-router-dom';
import axios from 'axios';
import {
   Box, Checkbox,
   FormControl,
   FormControlLabel,
   FormGroup,
   FormLabel,
   Grid,
   Radio,
   RadioGroup,
   Typography
} from "@mui/material";
import {v4 as uuidv4} from 'uuid';

const MenuPicture = (props) => {
   const {url1} = props;
   return (
      <Box>
         MenuPicture Here
      </Box>
   )
}

const OptionLabel = (props) => {
   const {optionGroup} = props;

   return (
      <Box mt={3}>
         <Grid container alignItems="center" justifyContent="space-between">
            <Grid item>
               <Typography variant="h6">{optionGroup.description}</Typography>
            </Grid>
            <Grid item>
               {optionGroup.necessary && (
                  // Grid container 내부에 Grid 는 모두 한 행의 각 열로 순서대로 배치된다.
                  // Grid container 가 없으면 각 열로 배치됨.
                  <Grid container alignItems="center">
                     <Grid>
                        <Typography variant="body1" ml={2}>
                           Must Select
                        </Typography>
                     </Grid>
                     <Grid>
                        <Typography variant="body1" ml={2}>
                           {optionGroup.maxSelectNumber}
                        </Typography>
                     </Grid>
                  </Grid>
               )}
            </Grid>
         </Grid>
      </Box>
   );
};
const CustomFormControlLabel = (props) => {
  const { option, optionGroupIndex, optionIndex, isChecked, onOptionChange } = props;

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
        <Checkbox checked={isChecked} onChange={() => onOptionChange(optionGroupIndex, optionIndex)} />
        <Typography variant="body2" ml={2}>
          {option.name}
        </Typography>
      </Box>
      <Typography variant="body2">{option.cost}</Typography>
    </Box>
  );
};


const MenuPage = (props) => {
   const location = useLocation()
   const {optionGroups} = location.state

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
   const [selectedOptions, setSelectedOptions] = useState({});

  const handleOptionChange = (optionGroupIndex, optionIndex) => {
    setSelectedOptions((prevSelectedOptions) => {
      const updatedOptions = { ...prevSelectedOptions };

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
   return (
      <Box display="flex" flexDirection="column" alignItems="center">
         <MenuPicture/>
         {optionGroups && optionGroups.map((optionGroup, optionGroupIndex) => (
            <Grid key={uuidv4()} container alignItems="center" >
                <FormControl key={uuidv4()} fullWidth={true} >
                  <OptionLabel key={uuidv4()} optionGroup={optionGroup}/>
                  <FormGroup>
                     {optionGroup.optionDtoList.map((option, optionIndex) => {
                        return (
                           <CustomFormControlLabel
                               key={uuidv4()}
                               option={option}
                               isChecked={selectedOptions && selectedOptions[optionGroupIndex] && selectedOptions[optionGroupIndex][optionIndex] === true}
                               optionGroupIndex={optionGroupIndex}
                               optionIndex={optionIndex}
                               onOptionChange={handleOptionChange}
                             />

                        )
                     })}
                  </FormGroup>
                </FormControl>

            </Grid>
         ))}
      </Box>
   )

};

export default MenuPage;

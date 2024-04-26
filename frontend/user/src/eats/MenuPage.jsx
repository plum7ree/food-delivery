import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import {Box} from "@mui/material";

const MenuPicture = (props) => {
  return (
     <Box>
        MenuPicture Here
     </Box>
  )
}

const OptionLabel = (props) => {
  const { optionGroup } = props;

  return (
    <Box mb={3}>
      <Typography variant="h6">{optionGroup.name}</Typography>
      <Typography variant="body1">
        Is Necessary: {optionGroup.isNecessary ? 'Must Select' : ''}
      </Typography>
      <Typography variant="body1">
        Is Duplicated Allowed: {optionGroup.isDuplicatedAllowed ? 'Duplication Ok' : ''}
      </Typography>
    </Box>
  );
};

const OptionSelector = (props) => {
   const { option } = props;

   return (
      <Box>
         <Typography variant="subtitle1">{option.name}</Typography>
         <Typography variant="body2">Cost: {option.cost}</Typography>
      </Box>
   )
}


const MenuPage = (props) => {
   var optionGroups = props.optionGroups
   return (
      <Box>
         <MenuPicture />
            {optionGroups.map((optionGroup, optionGroupIndex) => (
               <OptionLabel optionGroup={optionGroup}>
                  {optionGroup.options.map((option, index) => (
                     <OptionSelector option=option />
                  ))}
               </OptionLabel>
            ))}
      </Box>
   )

};

export default MenuPage;

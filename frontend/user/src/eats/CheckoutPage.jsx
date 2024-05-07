import React, {useEffect, useState} from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import {Box, Grid, Typography, Button, RadioGroup, FormControlLabel, Radio, Divider} from '@mui/material';
import {Container} from "@mui/system";
import {useSelector} from "react-redux";

      /**
       * menuItem = {
       *   "name": "Classic Burger",
       *   "description": "Delicious classic burger",
       *   "price": "10000",
       *   "pictureUrl": "burger_url1",
       *   "optionGroupDtoList": [
       *     {
       *       "description": "Select options",
       *       "maxSelectNumber": 1,
       *       "selectedIndicesList": [],
       *       "optionDtoList": [
       *         {
       *           "id": "f7c3b960-be7a-11eb-8529-0242ac130003",
       *           "name": "Cheese",
       *           "cost": "500"
       *         },
       *         {
       *           "id": "041d9d20-be7b-11eb-8529-0242ac130003",
       *           "name": "Bacon",
       *           "cost": "1000"
       *         },
       *         {
       *           "id": "0ca17480-be7b-11eb-8529-0242ac130003",
       *           "name": "Lettuce",
       *           "cost": "300"
       *         }
       *       ],
       *       "necessary": true
       *     },
       *     {
       *       "description": "Select options",
       *       "maxSelectNumber": 1,
       *       "selectedIndicesList": [],
       *       "optionDtoList": [
       *         {
       *           "id": "f7c3b960-be7a-11eb-8529-0242ac130003",
       *           "name": "Cheese",
       *           "cost": "500"
       *         },
       *         {
       *           "id": "041d9d20-be7b-11eb-8529-0242ac130003",
       *           "name": "Bacon",
       *           "cost": "1000"
       *         },
       *         {
       *           "id": "0ca17480-be7b-11eb-8529-0242ac130003",
       *           "name": "Lettuce",
       *           "cost": "300"
       *         }
       *       ],
       *       "necessary": false
       *     }
       *   ]
       * }
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
const renderSelectedOptions = (optionGroupDtoList, selectedOptions) => {
   return optionGroupDtoList.map((optionGroup, optionGroupIndex) => (
      <Grid container item key={optionGroupIndex}>
         {optionGroup.optionDtoList.map((option, optionIndex) => {
            if (selectedOptions[optionGroupIndex] && selectedOptions[optionGroupIndex][optionIndex]) {
               return (
                  <Grid container key={optionIndex} justifyContent="space-between">
                     <Grid item ml={3}>
                        {option.name}
                     </Grid>
                     <Grid item>
                        {option.cost}원
                     </Grid>
                  </Grid>
               );
            }
            return null;
         })}
      </Grid>
   ));
};

const calculateTotalPrice = ({optionGroupDtoList, selectedOptions}) => {
      let total = 0;
      optionGroupDtoList && optionGroupDtoList.map((optionGroup, optionGroupIndex) => {
         optionGroup.optionDtoList.map((option, optionIndex) => {
            if (selectedOptions[optionGroupIndex] && selectedOptions[optionGroupIndex][optionIndex]) {
               total += Number(option.cost);
            }
         });
      })
     return total;
   };

const CheckoutPage = () => {
   const location = useLocation();
   const navigate = useNavigate();
   const [paymentMethod, setPaymentMethod] = useState('credit_card');
   // selectedMenuItems = [{menuItem, selectedOptions}...]
   const selectedMenuItems = useSelector((state) => state.selectedMenu);
  const [totalPrice, setTotalPrice] = useState(0);


useEffect(() => {
    const calculatedTotalPrice = selectedMenuItems.reduce((total, { menuItem, selectedOptions }) => {
      const selectedOptionsPrice = calculateTotalPrice({ optionGroupDtoList: menuItem.optionGroupDtoList, selectedOptions });
      return total + parseInt(menuItem.price) + selectedOptionsPrice;
    }, 0);
    setTotalPrice(calculatedTotalPrice);
  }, [selectedMenuItems]);

   const handlePaymentMethodChange = (event) => {
      setPaymentMethod(event.target.value);
   };

   const handleCheckout = () => {
      // 결제 처리 로직 추가
      console.log('Checkout completed', {paymentMethod, totalPrice});
      navigate('/');
   };


   return (
      <Container maxWidth="sm">
         <Grid container direction="column" spacing={2}>
            <Grid item>
               <Typography variant="h4">Checkout</Typography>
            </Grid>
            <Grid container item direction="column">
               <Grid item alignSelf="flex-start">
               <Typography variant="h6">Order Summary</Typography>

               </Grid>
               {selectedMenuItems.map(({menuItem, selectedOptions}, index) => (
                  <Grid container item direction="column" key={index} pl={3} >
                     <Grid container item direction="row" justifyContent="space-between">
                           <Grid item >
                              <Typography variant="body1">{menuItem.name}</Typography>
                           </Grid>
                           <Grid item >
                              <Typography variant="body1">
                                 {menuItem.price}원
                              </Typography>
                           </Grid>
                        </Grid>
                        <Grid container item justifyContent="flex-start">
                           {renderSelectedOptions(menuItem.optionGroupDtoList, selectedOptions)}
                        </Grid>

                  </Grid>
               ))}
            </Grid>
            <Divider/>
               <Grid item alignSelf="flex-start">
                  <Typography variant="h6">Total</Typography>
               </Grid>
               <Grid container item justifyContent="flex-end">
                  <Typography variant="h6">
                     {totalPrice}원
                  </Typography>
               </Grid>
         </Grid>
         <Grid item>
            <Typography variant="h6">Select Payment Method</Typography>
            <RadioGroup value={paymentMethod} onChange={handlePaymentMethodChange}>
               <FormControlLabel value="credit_card" control={<Radio/>} label="Credit Card"/>
               <FormControlLabel value="toss" control={<Radio/>} label="Toss"/>
            </RadioGroup>
         </Grid>
         <Grid container item mt={2}>
            <Button variant="contained" color="success" fullWidth onClick={handleCheckout}>
               Pay
            </Button>
         </Grid>
      </Container>
   );
};

export default CheckoutPage;
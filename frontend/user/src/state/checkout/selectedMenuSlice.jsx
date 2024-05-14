import { createSlice } from "@reduxjs/toolkit";

const selectedMenuSlice = createSlice({
  name: "selectedMenu",
  initialState: [],
  reducers: {
    addMenu: (state, action) => {
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
      const { menuItem, selectedOptions } = action.payload;
      state.push({ menuItem, selectedOptions, quantity: 1 });
    },
    removeMenu: (state, action) => {
      const menuItemId = action.payload;
      const index = state.findIndex((item) => item.menuItem.id === menuItemId);
      if (index !== -1) {
        state.splice(index, 1);
      }
    },
    updateQuantity: (state, action) => {
      const { menuItemId, quantity } = action.payload;
      const menuItem = state.find((item) => item.menuItem.id === menuItemId);
      if (menuItem) {
        menuItem.quantity = quantity;
      }
    },

  },
});

export const selectTotalPrice = (state) => {
  return state.selectedMenu.reduce((total, item) => total + item.menuItem.price * item.quantity, 0);
};

export const selectCartItemCount = (state) => {
  return state.selectedMenu.length;
};

export const { addMenu,
  removeMenu,
  updateQuantity } = selectedMenuSlice.actions;

export default selectedMenuSlice.reducer;
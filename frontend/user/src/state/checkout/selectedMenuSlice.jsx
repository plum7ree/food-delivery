import {createSlice} from "@reduxjs/toolkit";
import {useSelector} from "react-redux";

const selectedMenuSlice = createSlice({
   name: "selectedMenu",
   /**
    * 선택된 메뉴의 선택되지 않은 옵션까지 포함된다.
    * 선택된 옵션의 index 는 따로 selectedOptions 에 저장된다.
    * {
    *    restaurantId: "",
    *    menus = [ 0: {
    *       menuItem = {
    *         "id": "uuid"
    *         "name": "Classic Burger",
    *         "description": "Delicious classic burger",
    *         "price": "10000",
    *         "pictureUrl": "burger_url1",
    *         "optionGroupDtoList": [
    *           {
    *             "description": "Select options",
    *             "maxSelectNumber": 1,
    *             "selectedIndicesList": [],
    *             "optionDtoList": [
    *               {
    *                 "id": "f7c3b960-be7a-11eb-8529-0242ac130003",
    *                 "name": "Cheese",
    *                 "cost": "500"
    *               },
    *               {
    *                 "id": "041d9d20-be7b-11eb-8529-0242ac130003",
    *                 "name": "Bacon",
    *                 "cost": "1000"
    *               },
    *               {
    *                 "id": "0ca17480-be7b-11eb-8529-0242ac130003",
    *                 "name": "Lettuce",
    *                 "cost": "300"
    *               }
    *             ],
    *             "necessary": true
    *            },
    *            {
    *                "description": "Select options",
    *                "maxSelectNumber": 1,
    *                "selectedIndicesList": [],
    *                "optionDtoList": [
    *                  {
    *                    "id": "f7c3b960-be7a-11eb-8529-0242ac130003",
    *                    "name": "Cheese",
    *                    "cost": "500"
    *                  },
    *                  {
    *                    "id": "041d9d20-be7b-11eb-8529-0242ac130003",
    *                    "name": "Bacon",
    *                    "cost": "1000"
    *                  },
    *                  {
    *                    "id": "0ca17480-be7b-11eb-8529-0242ac130003",
    *                    "name": "Lettuce",
    *                    "cost": "300"
    *                  }
    *                ],
    *                "necessary": false
    *             }
    *         ]
    *       },
    *
    *       selectedOptions = {
    *          optionGroupIndex0: {
    *             optionIndex0: true
    *          },
    *
    *          optionGroupIndex1: {
    *             optionIndex0: true
    *          }
    *       },
    *       // 선택된건 0 : true, 선택되지 않은건 아예 추가되지 않음 따라서 0: false 이런건 없음.
    *       // ex) { 0: { 0 : true }, 1: { 0 : true, 1: true}}
    *
    *       quantity: 1
    *    }]
    *  }
    *
    */
   initialState: {restaurantId: "", menus: []},
   reducers: {
      initRestaurantIfNeeded: (state, action) => {
         const {restaurantId} = action.payload;
         if (state.restaurantId !== restaurantId) {
            // 현재 restaurantId와 새로운 restaurantId가 다르면 state 초기화
            return {
               restaurantId: restaurantId,
               menus: [],
            };
         }
         // restaurantId가 같으면 state를 변경하지 않음
         return state;
      },
      addMenu: (state, action) => {
         const {menuItem, selectedOptions} = action.payload;
         state.menus.push({menuItem, selectedOptions, quantity: 1});
      },
      removeMenu: (state, action) => {
         const menuItemId = action.payload;
         const index = state.menus.findIndex((item) => item.menuItem.id === menuItemId);
         if (index !== -1) {
            state.splice(index, 1);
         }
      },
      updateQuantity: (state, action) => {
         const {menuItemId, quantity} = action.payload;
         const menuItem = state.menus.find((item) => item.menuItem.id === menuItemId);
         if (menuItem) {
            menuItem.quantity = quantity;
         }
      },

   },
});

export const selectCartItemCount = (store) => {
   console.log(store)
   return store.selectedMenu.menus.length;
};

/**
 * selectedOptions 을 바탕으로 선택된
 * [{
 *    menuId: "",
 *    quantity: 1,
 *    selectedOptions = [
 *       { optionId: "", quantity: 1}
 *    ]
 * }]
 * @param state
 */
export const selectedMenuWithOnlyIdAndQuantity = (state) => {
   return state.selectedMenu.menus.map(item => {
      const selectedOptions = Object.entries(item.selectedOptions).flatMap(([groupIndex, options]) =>
         Object.keys(options).map(optionIndex => {
            const optionId = item.menuItem.optionGroupDtoList[groupIndex].optionDtoList[optionIndex].id;
            const quantity = 1;
            return {optionId, quantity: quantity};
         })
      );

      return {
         menuId: item.menuItem.id,
         quantity: item.quantity,
         selectedOptions
      };
   });
};

export const constructOrderMenusDto = (orderId, restaurantId, menus) => {
   return {
      "orderId": orderId,
      "restaurantId": restaurantId,
      "menus": menus
   }
}

export const {
   initRestaurantIfNeeded,
   addMenu,
   removeMenu,
   updateQuantity
} = selectedMenuSlice.actions;

export default selectedMenuSlice.reducer;
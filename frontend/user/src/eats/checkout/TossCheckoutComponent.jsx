import React, {useEffect, useRef, useState} from "react";
import {loadPaymentWidget, ANONYMOUS} from "@tosspayments/payment-widget-sdk";
import {nanoid} from "nanoid";
import {Button, Grid} from "@mui/material";
import {useSelector} from "react-redux";
import {
   constructOrderMenusDto, selectedMenuWithOnlyIdAndQuantity
} from "../../state/checkout/selectedMenuSlice";
import axiosInstance from "../../state/axiosInstance";
import {v4 as uuidv4} from 'uuid';

// 구매자의 고유 아이디를 불러와서 customerKey로 설정하세요.
// 이메일・전화번호와 같이 유추가 가능한 값은 안전하지 않습니다.
const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
const customerKey = nanoid();

// const paymentWidget = PaymentWidget(widgetClientKey, PaymentWidget.ANONYMOUS) // 비회원 결제

export function TossCheckoutComponent() {
   const paymentWidgetRef = useRef(null);
   const paymentMethodsWidgetRef = useRef(null);
   const agreementWidgetRef = useRef(null);
   const [price, setPrice] = useState(1000);
   const mySelectedMenuWithOnlyIdAndQuantity = useSelector(selectedMenuWithOnlyIdAndQuantity);
   const credential = useSelector((state) => state.auth?.credential ?? null);
   const restaurantId = useSelector((state) => state.selectedMenu.restaurantId);

   useEffect(() => {
      (async () => {
         const paymentWidget = await loadPaymentWidget(widgetClientKey, customerKey); // 비회원 customerKey

         if (paymentWidgetRef.current == null) {
            paymentWidgetRef.current = paymentWidget;
         }

         /**
          * 결제창을 렌더링합니다.
          * @docs https://docs.tosspayments.com/reference/widget-sdk#renderpaymentmethods%EC%84%A0%ED%83%9D%EC%9E%90-%EA%B2%B0%EC%A0%9C-%EA%B8%88%EC%95%A1
          */
         const paymentMethodsWidget = paymentWidgetRef.current.renderPaymentMethods(
            "#payment-method",
            {value: price},
            {variantKey: "DEFAULT"}
         );

         /**
          * 약관을 렌더링합니다.
          * @docs https://docs.tosspayments.com/reference/widget-sdk#renderagreement%EC%84%A0%ED%83%9D%EC%9E%90-%EC%98%B5%EC%85%98
          */
         agreementWidgetRef.current = paymentWidgetRef.current.renderAgreement('#agreement', {variantKey: 'DEFAULT'});

         paymentMethodsWidgetRef.current = paymentMethodsWidget;
      })();
   }, []);

   const handlePaymentRequest = async () => {

      // 결제를 요청하기 전에 orderId, amount를 서버에 저장하세요.
      // 결제 과정에서 악의적으로 결제 금액이 바뀌는 것을 확인하는 용도입니다.
      const orderId = uuidv4();
      const dto = constructOrderMenusDto(orderId, restaurantId, mySelectedMenuWithOnlyIdAndQuantity)

      //TODO axios interceptor 로 바꾸자.
      await axiosInstance.post(`/user/api/pay/prepare`, dto, {
         headers: {
            "content-type": "application/json",
            Authorization: `Bearer ${credential}`,
         }
      })

      console.log(`${window.location.origin}/eats/checkout/success/toss`)
      const paymentWidget = paymentWidgetRef.current;

      if (paymentWidget === null) {
         console.error("paymentWidget is null");
      }

      try {
         // warning: debug mode 에서는 창 안열림.
         await paymentWidget?.requestPayment({
            orderId: orderId,
            orderName: "토스 티셔츠 외 2건",
            customerName: "김토스",
            customerEmail: "customer123@gmail.com",
            customerMobilePhone: "01012341234",
            // ex) http://localhost:5173/eats/checkout/success/toss?paymentType=NORMAL&orderId=hK1aqfeQl0BWhYhN0gHOh&paymentKey=tgen_202405131846479U9b3&amount=1000
            // successUrl: `${window.location.origin}/eats/checkout/success` + window.location.search,
            successUrl: `${window.location.origin}/eats/checkout/success`,
            failUrl: `${window.location.origin}/eats/checkout/fail`,
         });

      } catch (error) {
         console.error("Error requesting payment:", error);
      }
   };

   return (
      <div className="wrapper w-100">
         <div className="max-w-540 w-100">
            <div id="payment-method" className="w-100"/>
            <div id="agreement" className="w-100"/>
            <div className="btn-wrapper w-100">
               {/* 결제하기 버튼 */}
               <Grid container item mt={2}>
                  <Button variant="contained"
                          color="success"
                          fullWidth
                          onClick={handlePaymentRequest}>
                     Pay
                  </Button>
               </Grid>
            </div>
         </div>
      </div>
   );
}
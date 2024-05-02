import React, {useState} from "react";
import {PaymentWidget} from "@tosspayments/payment-sdk";

const TossPay = () => {
   const [couponApplied, setCouponApplied] = useState(false);
   const amount = 50000;

   const widgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
   const customerKey = "4Gz3jRdxrPXS8QEwaNFHs";
   const paymentWidget = PaymentWidget(widgetClientKey, customerKey);

   const handleCouponChange = () => {
      setCouponApplied(!couponApplied);
      if (!couponApplied) {
         paymentMethodWidget.updateAmount(amount - 5000);
      } else {
         paymentMethodWidget.updateAmount(amount);
      }
   };

   const handlePaymentButtonClick = () => {
      paymentWidget.requestPayment({
         orderId: "1W_pCfO4rzG9szJEcThKe",
         orderName: "토스 티셔츠 외 2건",
         successUrl: window.location.origin + "/success",
         failUrl: window.location.origin + "/fail",
         customerEmail: "customer123@gmail.com",
         customerName: "김토스",
         customerMobilePhone: "01012341234",
      });
   };

   return (
      <div>
         <div>
            <input
               type="checkbox"
               id="coupon-box"
               checked={couponApplied}
               onChange={handleCouponChange}
            />
            <label htmlFor="coupon-box">5,000원 쿠폰 적용</label>
         </div>
         <div id="payment-method"/>
         <div id="agreement"/>
         <button id="payment-button" onClick={handlePaymentButtonClick}>
            결제하기
         </button>
      </div>
   );
};

export default TossPay;

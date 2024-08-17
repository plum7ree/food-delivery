import {useEffect, useState} from "react";
import {useNavigate, useSearchParams} from "react-router-dom";
import './style.css'
import {useDispatch, useSelector} from "react-redux";
import {SERVER_URL} from "../../state/const";
import axiosInstance from "../../state/axiosInstance";
import {asyncGetAuth} from "../../state/authSlice";

export function CheckoutSuccessPage() {
   const [isConfirmed, setIsConfirmed] = useState(false);
   const [searchParams] = useSearchParams();
   const paymentType = searchParams.get('paymentType');
   const orderId = searchParams.get('orderId');
   const paymentKey = searchParams.get('paymentKey');
   const amount = searchParams.get('amount');

   const navigate = useNavigate();
   const dispatch = useDispatch();
   const credential = useSelector((state) => state.auth?.credential);
   const getAuthStatus = useSelector((state) => state.auth?.getAuthStatus);

   const [isButtonEnabled, setButtonEnabled] = useState(false);
   // step1. getAuth 부르고, 아래의 useEffect 에서 getAuthStatus 관찰.
   useEffect(() => {
      dispatch(asyncGetAuth());
   }, [dispatch]);

   // step2. getAuthStatus 가 fulfilled 이면, credential 로드 된거임.
   useEffect(() => {
      if (getAuthStatus === 'fulfilled' && credential) {
         setButtonEnabled(true)
      } else {
         setButtonEnabled(false)
      }
   }, [getAuthStatus, credential]);

   async function confirmPayment() {
      const dto = JSON.stringify({
         paymentKey,
         orderId,
         amount
      });

      try {
         const response = await axiosInstance.post(`/user/api/pay/confirm`, dto, {
            headers: {
               "content-type": "application/json",
               Authorization: `Bearer ${credential}`,
            }
         });

         if (response.status === 200) {
            console.log('Payment successful');
            setIsConfirmed(true);
            navigate('/eats');
         } else {
            console.error('Payment confirmation failed');
            // 3초 후에 /eats 페이지로 이동
            setTimeout(() => {
               navigate('/eats');
            }, 3000);
         }
      } catch (error) {
         console.error('Error confirming payment:', error);
         // 3초 후에 /eats 페이지로 이동
         setTimeout(() => {
            navigate('/eats');
         }, 3000);
      }
   }

   return (
      <div className="wrapper w-100">
         {isConfirmed ? (
            <div
               className="flex-column align-center confirm-success w-100 max-w-540"
               style={{
                  display: "flex"
               }}
            >
               <img
                  src="https://static.toss.im/illusts/check-blue-spot-ending-frame.png"
                  width="120"
                  height="120"
               />
               <h2 className="title">결제를 완료했어요</h2>
               <div className="response-section w-100">
                  <div className="flex justify-between">
                     <span className="response-label">결제 금액</span>
                     <span id="amount" className="response-text">
                {amount}
              </span>
                  </div>
                  <div className="flex justify-between">
                     <span className="response-label">주문번호</span>
                     <span id="orderId" className="response-text">
                {orderId}
              </span>
                  </div>
                  <div className="flex justify-between">
                     <span className="response-label">paymentKey</span>
                     <span id="paymentKey" className="response-text">
                {paymentKey}
              </span>
                  </div>
               </div>

               <div className="w-100 button-group">

                  <div className="flex" style={{gap: "16px"}}>
                     <a
                        className="btn w-100"
                        href="https://developers.tosspayments.com/sandbox"
                     >
                        다시 테스트하기
                     </a>
                     <a
                        className="btn w-100"
                        href="https://docs.tosspayments.com/guides/payment-widget/integration"
                        target="_blank"
                        rel="noopner noreferer"
                     >
                        결제 연동 문서가기
                     </a>
                  </div>
               </div>
            </div>
         ) : (
            <div className="flex-column align-center confirm-loading w-100 max-w-540">
               <div className="flex-column align-center">
                  <img
                     src="https://static.toss.im/lotties/loading-spot-apng.png"
                     width="120"
                     height="120"
                  />
                  <h2 className="title text-center">결제 요청까지 성공했어요.</h2>
                  <h4 className="text-center description">결제 승인하고 완료해보세요.</h4>
               </div>
               <div className="w-100">
                  <button className="btn primary w-100" onClick={confirmPayment}  disabled={!isButtonEnabled}>
                     결제 승인하기
                  </button>
               </div>
            </div>
         )}
      </div>
   );
}
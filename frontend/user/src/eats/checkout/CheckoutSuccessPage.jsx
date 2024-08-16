import {useState} from "react";
import {useNavigate, useSearchParams} from "react-router-dom";
import './style.css'
import {useSelector} from "react-redux";
import {SERVER_URL} from "../../state/const";
import axiosInstance from "../../state/axiosInstance";

export function CheckoutSuccessPage() {
   const [isConfirmed, setIsConfirmed] = useState(false);
   const [searchParams] = useSearchParams();
   const paymentType = searchParams.get('paymentType');
   const orderId = searchParams.get('orderId');
   const paymentKey = searchParams.get('paymentKey');
   const amount = searchParams.get('amount');
   console.log("checkout success page")
   const navigate = useNavigate();
   const credential = useSelector((state) => state.auth?.credential ?? null);

   async function confirmPayment() {
      // TODO: API를 호출해서 서버에게 paymentKey, orderId, amount를 넘겨주세요.
      // 서버에선 해당 데이터를 가지고 승인 API를 호출하면 결제가 완료됩니다.
      // https://docs.tosspayments.com/reference#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8

      const dto = JSON.stringify({
         paymentKey,
         orderId,
         amount
      })
      console.assert(credential != null)
      const response = await axiosInstance.post(`/user/api/pay/confirm`, dto, {
         headers: {
            "content-type": "application/json",
            Authorization: `Bearer ${credential}`,
         }
      })
      if (response.ok) {
         console.log('Payment successful');
         setIsConfirmed(true);
         navigate('/eats');

      } else {
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
                  <button className="btn primary w-100" onClick={confirmPayment}>
                     결제 승인하기
                  </button>
               </div>
            </div>
         )}
      </div>
   );
}
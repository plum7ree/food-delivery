package com.example.eatsorderapplication.service.saga;

import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EatsOrderSaga {

    public SagaStatus updateSagaStatus(SagaStatus sagaStatus, OrderStatus newEvent) {
        switch (sagaStatus) {
            case STARTED:
                // 결제 완료
                if (newEvent == OrderStatus.PAYMENT_COMPLETED) {
                    return SagaStatus.PROCESSING;
                    // 사용자가 주문취소
                } else if (newEvent == OrderStatus.USER_CANCELED) {
                    // 결제 취소 완료
                    return SagaStatus.COMPENSATED;
                }
                break;
            case PROCESSING:
                // restaurant 에서 주문 승인.
                if (newEvent == OrderStatus.RESTAURANT_APPROVED) {
                    return SagaStatus.SUCCEEDED;
                    // restaurant 에서 주문 취소
                } else if (newEvent == OrderStatus.RESTAURANT_REJECTED) {
                    // 이미 paid 는 끝난 상태이고, restaurant 에서 주문 거부한 상태이다.
                    // payment service 에 cancel 요청 보내야함.
                    return SagaStatus.COMPENSATING;
                }
                break;
            // 주문 취소되서 결제 취소를 요청하는 단계.
            case COMPENSATING:
                // 결제 취소 완료
                if (newEvent == OrderStatus.PAYMENT_CANCELLED) {
                    // 취소 완료.
                    return SagaStatus.COMPENSATED;
                }
                break;
            case FAILED:
            case SUCCEEDED:
                // 결제 취소 완료상태.
            case COMPENSATED:
                // 이미 최종 상태이므로 변경 없음
                return sagaStatus;
        }

        // 상태 변경이 없는 경우 또는 정의되지 않은 전이의 경우
        return sagaStatus;
    }


}

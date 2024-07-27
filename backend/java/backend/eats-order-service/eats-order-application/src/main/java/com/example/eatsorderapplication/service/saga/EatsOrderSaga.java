package com.example.eatsorderapplication.service.saga;

import com.example.commondata.domain.aggregate.valueobject.OrderStatus;
import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import com.example.eatsorderdataaccess.entity.OrderEntity;
import com.example.eatsorderdataaccess.mapper.RepositoryEntityDataMapper;
import com.example.eatsorderdataaccess.repository.RestaurantApprovalRequestOutboxRepository;
import com.example.eatsorderdomain.data.domainentity.Order;
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
                if (newEvent == OrderStatus.PAYMENT_COMPLETED) {
                    return SagaStatus.PROCESSING;
                } else if (newEvent == OrderStatus.CALLER_CANCELLED) {
                    return SagaStatus.COMPENSATED;
                }
                break;
            case PROCESSING:
                if (newEvent == OrderStatus.CALLEE_APPROVED) {
                    return SagaStatus.SUCCEEDED;
                } else if (newEvent == OrderStatus.CALLEE_REJECTED) {
                    // 이미 paid 는 끝난 상태이고, restaurant 에서 주문 거부한 상태이다.
                    // payment service 에 cancel 요청 보내야함.
                    return SagaStatus.COMPENSATING;
                }
                break;
            case COMPENSATING:
                if (newEvent == OrderStatus.PAYMENT_CANCELLED) {
                    return SagaStatus.COMPENSATED;
                }
                break;
            case FAILED:
            case SUCCEEDED:
            case COMPENSATED:
                // 이미 최종 상태이므로 변경 없음
                return sagaStatus;
        }

        // 상태 변경이 없는 경우 또는 정의되지 않은 전이의 경우
        return sagaStatus;
    }


}

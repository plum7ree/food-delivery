package com.example.eatsorderapplication.service;

import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderapplication.messaging.publisher.OrderEventOutboxService;
import com.example.eatsorderapplication.service.saga.EatsOrderSaga;
import com.example.eatsorderdataaccess.entity.OrderEntity;
import com.example.eatsorderdataaccess.mapper.EntityDtoMapper;
import com.example.eatsorderdataaccess.repository.OrderRepository;
import com.example.eatsorderdataaccess.repository.RestaurantApprovalRequestOutboxRepository;
import com.example.eatsorderdomain.data.dto.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final EatsOrderSaga eatsOrderSaga;


    //    private final DataMapper dataMapper;
    private final RestaurantApprovalRequestOutboxRepository restaurantApprovalRequestOutboxRepository;
    private final OrderEventOutboxService orderEventOutboxService;

    /**
     * ç
     * OrderDomainObject 가 RestaurantApprovalRequestEntity 에 저장된 payload 이며,
     * OrderDomainObject 와 RequestAvroModel 는 이론적으로 같아야함.
     * 다만, producer 를 보내는데 RequestAvroModel 를 써야하며,
     * DDD 패턴상 메모리에 상주할때는 OrderDomainObject 를 써야함.
     *
     * @param createOrderRequest
     * @return
     */
    @Transactional
    public Mono<EatsOrderResponseDto> createAndSaveOrder(CreateOrderRequest createOrderRequest) {
        OrderEntity orderEntity = EntityDtoMapper.createOrderRequestToOrderEntity(createOrderRequest);
        // 여기에는 domain object 가 존재하면 안됨. 제일 로직 코어에 존재해야함. dto 가 대신 피룡.
        return orderRepository.save(orderEntity)
            .map(EntityDtoMapper::orderEntityToRestaurantApprovalOutboxEntity)
            .flatMap(restaurantApprovalRequestOutboxRepository::upsert)
            .map(entity -> EatsOrderResponseDto.builder().build());
    }


//    /**
//     * order 상태를 approved 으로 업데이트
//     *
//     * @param orderId
//     * @return
//     */
//    @Transactional
//    public Mono<Order> completeRestaurantApproval(UUID orderId) {
//        return orderRepository.updateStatus(orderId, OrderStatus.RESTAURANT_APPROVED.name())
//            .filter(updatedRows -> updatedRows > 0)
//            .flatMap(updatedRows -> orderRepository.findById(orderId));  // 업데이트 후 주문 조회


//        // TODO 이것들 다 여기서 할 필요가 없다.
//        // order approval 은 restaurant approval service 에서 수행해야하고
//        // outbox 마킹또한 메시지 보낼때 하는것이다.
//        // 1. 이미 APPROVED 한 상태로 업데이트 했다면, 아무 작업도 하지 않음
//        return orderApprovalRepository.
//            findByOrderIdAndStatus(order.getId().getValue(), RestaurantApprovalStatus.APPROVED.name())
//            .switchIfEmpty(Mono.defer(() -> {
//                // 2. 그게 아니라면 APPROVED 로 업데이트.
//                var entity = RepositoryEntityDataMapper
//                    .orderToOrderApproval(order, RestaurantApprovalStatus.APPROVED);
//                return orderApprovalRepository.save(entity);
//            }))
//            // 3. Outbox 에 event 저장
//            .then(updateOutboxRepositories(order, sagaId));

}

//    /**
//     * Outbox 에 이벤트 저장
//     *
//     * @param order
//     * @param sagaId
//     * @return
//     */
//    public Mono<Void> updateOutboxRepositories(Order order, UUID sagaId) {
//        // sagaId 기반으로 탐색. TODO orderId 기반 탐색이랑 같지 않나?
//        return restaurantApprovalRequestOutboxRepository.findById(sagaId)
//            .flatMap(outboxEntity -> {
//                SagaStatus currSagaStatus = SagaStatus.valueOf(outboxEntity.getSagaStatus());
//                SagaStatus newSagaStatus = eatsOrderSaga.updateSagaStatus(currSagaStatus, order.getOrderStatus());
//                outboxEntity.setSagaStatus(newSagaStatus.name());
//
//                return restaurantApprovalRequestOutboxRepository.upsert(outboxEntity)
//                    .thenReturn(Pair.of(currSagaStatus, newSagaStatus));
//
//            })
//            .flatMap(pair -> {
//                var currSagaStatus = pair.getFirst();
//                var newSagaStatus =pair.getSecond();
//                // saga id, status 현재 상태인것 찾고
//                return paymentOutboxRepository.findBySagaIdAndSagaStatus(sagaId.toString(), currSagaStatus.name())
//                        .flatMap(paymentOutboxMessageEntity -> {
//                            // 상태 upsert
//                            paymentOutboxMessageEntity.setSagaStatus(newSagaStatus.name());
//                            return paymentOutboxRepository.upsert(paymentOutboxMessageEntity);
//                        });
//                }
//            )
//            .then();
//    }

}

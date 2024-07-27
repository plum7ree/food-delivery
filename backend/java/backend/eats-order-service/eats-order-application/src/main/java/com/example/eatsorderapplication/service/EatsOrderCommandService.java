package com.example.eatsorderapplication.service;

import com.example.commondata.domain.aggregate.valueobject.OrderStatus;
import com.example.commondata.domain.aggregate.valueobject.OutboxStatus;
import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import com.example.eatsorderapplication.adapter.RestaurantAdapter;
import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderconfigdata.EatsOrderServiceConfigData;
import com.example.eatsorderdataaccess.entity.OrderEntity;
import com.example.eatsorderdataaccess.entity.RestaurantApprovalOutboxMessageEntity;
import com.example.eatsorderdataaccess.mapper.RepositoryEntityDataMapper;
import com.example.eatsorderdataaccess.repository.OrderRepository;
import com.example.eatsorderdataaccess.repository.RestaurantApprovalRequestOutboxRepository;
import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.eatsorderdomain.data.dto.CreateOrderCommandDto;
import com.example.eatsorderdomain.data.mapper.DtoDataMapper;
import com.example.kafka.avro.model.RequestAvroModel;
import com.example.kafkaproducer.KafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static com.example.commondata.domain.aggregate.valueobject.SagaType.EATS_ORDER;


@Service
@RequiredArgsConstructor
@Slf4j
public class EatsOrderCommandService {

    //    private final DataMapper dataMapper;
    private final OrderRepository orderRepository;
    private final RestaurantApprovalRequestOutboxRepository restaurantApprovalRequestOutboxRepository;


    /**
     * ç
     * OrderDomainObject 가 RestaurantApprovalRequestEntity 에 저장된 payload 이며,
     * OrderDomainObject 와 RequestAvroModel 는 이론적으로 같아야함.
     * 다만, producer 를 보내는데 RequestAvroModel 를 써야하며,
     * DDD 패턴상 메모리에 상주할때는 OrderDomainObject 를 써야함.
     *
     * @param createOrderCommandDto
     * @return
     */
    @Transactional
    public EatsOrderResponseDto createAndSaveOrder(CreateOrderCommandDto createOrderCommandDto) {
        try {
            Order order = DtoDataMapper.orderDtoToOrder(createOrderCommandDto);

            log.info("try to save a call. Id: {}", order.getId().getValue());
            {
                OrderEntity orderEntity = RepositoryEntityDataMapper.orderToOrderEntity(order);
                orderRepository.save(orderEntity);
                orderRepository.save(orderEntity);

            }

            {
                var entity = RepositoryEntityDataMapper.orderToRestaurantApprovalOutboxMessageEntity(
                    order,
                    UUID.randomUUID(),
                    EATS_ORDER.name(),
                    OutboxStatus.STARTED,
                    SagaStatus.STARTED
                );
                restaurantApprovalRequestOutboxRepository.save(entity);
            }


            return EatsOrderResponseDto.builder().orderStatus(order.getOrderStatus()).callTrackingId(order.getTrackingId().getValue()).build();

        } catch (Exception e) {
            log.error(e.toString());
            //TODO save error into error db.
        }
        return null;
    }


}

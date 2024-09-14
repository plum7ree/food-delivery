package com.example.eatsorderdataaccess.repository;

import com.example.commondata.domain.aggregate.valueobject.Address;
import com.example.commondata.domain.events.order.OrderStatus;
import com.example.commondata.dto.order.AddressDto;
import com.example.commondata.dto.order.UserAddressDto;
import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.eatsorderdomain.data.domainentity.OrderItem;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
public class R2DBCOrderRepository implements OrderRepository {

    private final DatabaseClient databaseClient;

    public R2DBCOrderRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }


    public Mono<Order> saveOrderWithDetails(Order order) {
        return saveOrder(order)
            .then(saveOrderAddress(order.getAddress(), order.getId()))
            .then(saveOrderItems(order.getItems(), order.getId()))
            .thenReturn(order);
    }

    private Mono<Long> saveOrder(Order order) {
        String query = "INSERT INTO orders (id, customer_id, restaurant_id, price, order_status, failure_messages) " +
            "VALUES (:id, :customerId, :restaurantId, :price, :orderStatus, :failureMessages)";

        return databaseClient.sql(query)
            .bind("id", order.getId())
            .bind("customerId", order.getCallerId())
            .bind("restaurantId", order.getCalleeId())
            .bind("price", order.getPrice())
            .bind("orderStatus", order.getOrderStatus().toString())
            .bind("failureMessages", order.getFailureMessages() != null ? String.join(",", order.getFailureMessages()) : null)
            .fetch()
            .rowsUpdated();
    }

    private Mono<Long> saveOrderAddress(Address address, UUID orderId) {
        String query = "INSERT INTO order_address (order_id, street, postal_code, city, lat, lon) " +
            "VALUES (:orderId, :street, :postalCode, :city, :lat, :lon)";

        return databaseClient.sql(query)
            .bind("orderId", orderId)
            .bind("street", address.getStreet())
            .bind("postalCode", address.getPostalCode())
            .bind("city", address.getCity())
            .bind("lat", address.getLat())
            .bind("lon", address.getLon())
            .fetch()
            .rowsUpdated();
    }

    private Mono<Long> saveOrderItems(List<OrderItem> items, UUID orderId) {
        return Flux.fromIterable(items)
            .flatMap(item -> {
                String query = "INSERT INTO order_items (order_id, product_id, price, quantity, sub_total) " +
                    "VALUES (:orderId, :productId, :price, :quantity, :subTotal)";

                return databaseClient.sql(query)
                    .bind("orderId", orderId)
                    .bind("productId", item.getId())
                    .bind("price", item.getPrice())
                    .bind("quantity", item.getQuantity())
                    .bind("subTotal", item.getSubTotal())
                    .fetch()
                    .rowsUpdated();
            })
            .collectList()
            .map(result -> (long) result.size()); // 전체 삽입된 row 수 리턴
    }

    @Override
    public Mono<Long> updateStatus(UUID id, String orderStatus) {

        String query = "UPDATE orders SET order_status = :orderStatus WHERE id = :id";

        return databaseClient.sql(query)
            .bind("id", id)
            .bind("orderStatus", orderStatus)
            .fetch()
            .rowsUpdated();
    }


    @Override
    public Mono<Order> findById(UUID id) {
        String query = "SELECT id, customer_id, restaurant_id, price, order_status, failure_messages " +
            "FROM orders WHERE id = :id";

        return databaseClient.sql(query)
            .bind("id", id)
            .map(row -> {

                    var order = Order.builder()
                        .id(row.get("id", UUID.class))
                        .callerId(row.get("customer_id", UUID.class))
                        .calleeId(row.get("restaurant_id", UUID.class))
                        .price(row.get("price", Double.class))
                        .orderStatus(OrderStatus.valueOf(row.get("order_status", String.class)))
                        .build();
                    return order;
                }
            )
            .one();  // Retrieve a single row
    }

    @Override
    public Mono<UserAddressDto> findUserAddressDtoByOrderId(UUID orderId) {
        // OrderEntity 를 가져오는 쿼리
        String query = "SELECT t1.id, t1.user_id, t2.street, t2.postal_code, t2.city, t2.lon, t2.lat " +
            "FROM orders t1" +
            "INNER JOIN order_address t2 ON t1.id = t2.order_id " +
            "WHERE t1.id = :order_id";


        return databaseClient.sql(query)
            .bind("order_id", orderId)
            .map(row -> UserAddressDto.builder()
                .userId(row.get("user_id", String.class))
                .address(AddressDto.builder()
                    .street(row.get("street", String.class))
                    .postalCode(row.get("postal_code", String.class))
                    .city(row.get("city", String.class))
                    .lat(row.get("lat", Double.class))
                    .lon(row.get("lon", Double.class))
                    .build())
                .build())
            .one();

    }

}

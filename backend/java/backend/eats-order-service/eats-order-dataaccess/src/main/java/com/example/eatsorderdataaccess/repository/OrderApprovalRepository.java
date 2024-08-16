package com.example.eatsorderdataaccess.repository;

import com.example.commondata.domain.aggregate.valueobject.RestaurantApprovalStatus;
import com.example.eatsorderdataaccess.entity.OrderApprovalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

//TODO native query 는 영속성 컨텍스트 유지 안함.
// "만일 필요하다면", entityManager 활용해서 쿼리 작성해야함.
@Repository
public interface OrderApprovalRepository extends JpaRepository<OrderApprovalEntity, UUID> {

    /**
     * @param orderId String
     * @param status  String enum 말고 반드시 String 으로 받아야한다!
     * @return
     */
    @Query(value = "SELECT * FROM order_approval " +
        "WHERE order_id = :orderId " +
        "and status = :status"
        , nativeQuery = true)
    Optional<OrderApprovalEntity> findByOrderIdAndStatus(@Param("orderId") UUID orderId,
                                                         @Param("status") String status);

    // modifying 안붙이면 insert 에서 result return set 없다고 에러뜸.
    @Modifying(clearAutomatically = true)
    @Query(value =
        "INSERT INTO order_approval (id, order_id, restaurant_id, status) " +
            "VALUES (:#{#entity.id}, :#{#entity.orderId}, :#{#entity.restaurantId}, :#{#entity.status}) " +
            "ON CONFLICT (id) DO UPDATE SET " +
            "order_id = EXCLUDED.order_id, " +
            "restaurant_id = EXCLUDED.restaurant_id, " +
            "status = EXCLUDED.status",
        nativeQuery = true)
    void upsert(@Param("entity") OrderApprovalEntity entity);

    @Override
    @Transactional
    default OrderApprovalEntity save(OrderApprovalEntity entity) {
        upsert(entity);
        return findById(entity.getId()).orElse(entity);
    }
}


//@Repository
//public class OrderApprovalRepositoryImpl implements OrderApprovalRepositoryCustom {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Override
//    @Transactional
//    public OrderApprovalEntity saveOrUpdate(OrderApprovalEntity entity) {
//        // @GeneratedValue 애노테이션을 사용하여 ID를 자동 생성하도록 설정한 경우, 새 엔티티의 ID는 persist() 호출 전까지 null
//        if (entity.getId() == null) {
//            // 새로운 엔티티인 경우
//            entityManager.persist(entity);
//            return entity;
//        } else {
//            // 이미 존재하는 엔티티인 경우
//            OrderApprovalEntity managedEntity = entityManager.find(OrderApprovalEntity.class, entity.getId());
//            if (managedEntity != null) {
//                // 엔티티가 이미 존재하면 필드 업데이트
//                managedEntity.setOrderId(entity.getOrderId());
//                managedEntity.setRestaurantId(entity.getRestaurantId());
//                managedEntity.setApprovalStatus(entity.getApprovalStatus());
//                return managedEntity;
//            } else {
//                // 엔티티가 데이터베이스에 없으면 merge 사용
//                return entityManager.merge(entity);
//            }
//        }
//    }
//}
//
//@Repository
//public interface OrderApprovalRepository extends JpaRepository<OrderApprovalEntity, UUID>, OrderApprovalRepositoryCustom {
//}
//
//interface OrderApprovalRepositoryCustom {
//    OrderApprovalEntity saveOrUpdate(OrderApprovalEntity entity);
//}
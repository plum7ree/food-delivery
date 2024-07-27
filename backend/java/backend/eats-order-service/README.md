This api service is called from callback from PayController.java in 'user' application.
After toss payment's confirm, this service will send a request to a restaurant to confirm the order request.
After order request approved, the entire transaction is completed.

If a restaurant reject the order, this should cancel the payment.

예외 케이스들

- caller 가 cancel 요청을 했는데 callee(restaurant) 이 요청을 승인해 버리면?
  db 에 OrderStatus, SagaStatus 를 바탕으로 판단해야함.

Domain

- Restaurant
    - Product
- Order
    - OrderItems
- Customer

<b>SagaStatus<b/>
: 전체적인 주문의 흐름을 기록. eats-order-application 에서 관리됨.
: Outbox 엔티티들에 저장된다.

- STARTED,
- FAILED,
- SUCCEEDED, : 모든 주문 완료 상태 (end state)
- PROCESSING,
- COMPENSATING : 결제 후 음식점에서 주문을 취소한 상태지만 아직 환불은 완료 안된 상태.
- COMPENSATED : 환불 완료 상태 (end state)

<b>OutboxStatus</b>
: 스케쥴러가 메시지를 보내졌는지 기록 하기 위한 것.
각각의 서비스 (eats-order-application, payment-service, restaurant-approval-request-service) 에서 관리.

- PENDING
- STARTED
- FAILED

<b>OrderStatus</b>
: 각각의 서비스에서 발행하는 SagaStatus 상태 변화를 위한 이벤트.
: 이벤트 역할도 하지만 Order 엔티티의 상태를 알려주는 역할도 한다.
: TODO: 이벤트 역할은 다른 객체로 만들자.

- CALLER_CANCELLED
- CALLEE_APPROVED
- CALLEE_REJECTED
- PAYMENT_COMPLETED
- PAYMENT_CANCELLED

<b>주문 처리 상태 머신</b>

| SagaStatus | 이벤트                     | next SagaStatus | 액션             |
|:-----------|:------------------------|:----------------|:---------------|
| STARTED    | PAYMENT_COMPLETED 결제 완료 | PROCESSING      | 음식점에게 승인 요청 전송 |
| PROCESSING | CALLEE_APPROVED 주문 수락   | SUCCEEDED       | 전체 주문 완료       |

Optimistic Lock

- @Version

EatsOrderApplication

1. save order into db OrderStatus.PENDING
2. save event outbox message into db

Scheduler

1. 먼저 저장된 outbox message 들을 다시 불러온다. OutboxStatus.STARTED, SagaStatus.STARTED,COMPENSATING,
2. avro message 생성하고 kafka 메시지 전송. 이때 콜백을 등록.
3. if kafka producer got ack from a broker, save outbox message with OutboxStatus.COMPLETED.
4. if kafka producer throws an error, save outbox message with OutboxStatus.FAILED
5. clean scheduler 는 OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.FAILED, SagaStatus.COMPENSATED 된 끝 상태인 엔티티들을
   주기적으로 삭제.
6. 진행 중 (SagaStatus.COMPENSATING, SagaStatus.PROCESSING) 엔티티들은 지워지면 안된다.
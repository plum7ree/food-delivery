package com.example.commondata.domain.events;

import java.util.UUID;

public interface OrderSaga extends Saga {

    /*
        Intentionally using UUID to keep things simple. Prefer record OrderId(UUID id){}
    */
    UUID orderId();

}

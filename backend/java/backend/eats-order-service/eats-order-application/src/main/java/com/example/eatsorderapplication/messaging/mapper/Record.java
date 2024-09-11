package com.example.eatsorderapplication.messaging.mapper;

import reactor.kafka.receiver.ReceiverOffset;

public record Record<T>(String key,
                        T message,
                        ReceiverOffset acknowledgement) {
}

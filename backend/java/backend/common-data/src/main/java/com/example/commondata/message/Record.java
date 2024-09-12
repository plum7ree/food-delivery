package com.example.commondata.message;

import reactor.kafka.receiver.ReceiverOffset;

public record Record<T>(String key,
                        T message,
                        ReceiverOffset acknowledgement) {
}

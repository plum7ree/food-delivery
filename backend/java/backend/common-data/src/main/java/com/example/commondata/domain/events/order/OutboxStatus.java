package com.example.commondata.domain.events.order;

public enum OutboxStatus {
    CREATED, // a message created
    SENT, // a message sent successfully
    FAILED // failed to send an outbox message
}

package com.example.commondata.domain.events.order;

public enum OutboxStatus {
    SUCCESS, // a message sent successfully
    FAILED // failed to send an outbox message
}

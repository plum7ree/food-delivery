package com.example.calldomain.data.event;

import com.example.commondata.domain.event.DomainEvent;

public class EmptyEvent implements DomainEvent<Void> {
    @Override
    public void fire() {

    }
}

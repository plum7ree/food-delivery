package com.example.eatsorderdomain.data.event;

import com.example.commondata.domain.event.DomainEvent;

public class EmptyEvent implements DomainEvent<Void> {
    @Override
    public void fire() {

    }
}

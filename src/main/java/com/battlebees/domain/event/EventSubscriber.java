package com.battlebees.domain.event;

public interface EventSubscriber {
    void onEvent(DomainEvent event);
}

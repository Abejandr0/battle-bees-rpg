package com.battlebees.domain.event;

public interface EventPublisher {
    void publish(DomainEvent event);
}

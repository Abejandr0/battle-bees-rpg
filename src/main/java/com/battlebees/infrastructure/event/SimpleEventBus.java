package com.battlebees.infrastructure.event;

import com.battlebees.domain.event.DomainEvent;
import com.battlebees.domain.event.EventPublisher;
import com.battlebees.domain.event.EventSubscriber;
import java.util.ArrayList;
import java.util.List;

public class SimpleEventBus implements EventPublisher {
    private final List<EventSubscriber> subscribers = new ArrayList<>();
    
    // For web frontend pulling events
    private final List<DomainEvent> recentEvents = new ArrayList<>();

    public void subscribe(EventSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void publish(DomainEvent event) {
        recentEvents.add(event);
        for (EventSubscriber sub : subscribers) {
            sub.onEvent(event);
        }
    }

    public List<DomainEvent> pollEvents() {
        List<DomainEvent> eventsToReturn = new ArrayList<>(recentEvents);
        recentEvents.clear();
        return eventsToReturn;
    }
}

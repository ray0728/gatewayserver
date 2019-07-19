package com.rcircle.service.gateway.events.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface FeedBackSink {
    public String TOPIC = "feedback";

    @Input(TOPIC)
    SubscribableChannel input();
}

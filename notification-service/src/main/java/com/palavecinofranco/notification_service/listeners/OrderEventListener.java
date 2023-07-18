package com.palavecinofranco.notification_service.listeners;

import com.palavecinofranco.notification_service.events.OrderEvent;
import com.palavecinofranco.notification_service.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {

    @KafkaListener(topics = "orders-topic")
    public void handleOrderNotifications(String message){
        var orderEvent = JsonUtils.fromJson(message, OrderEvent.class);

        //Send email to customer, send sms to customers, etc.
        //Notify another microservices
        log.info("Order {} event received for order : {} with items", orderEvent.orderStatus(), orderEvent.itemCount());
    }
}

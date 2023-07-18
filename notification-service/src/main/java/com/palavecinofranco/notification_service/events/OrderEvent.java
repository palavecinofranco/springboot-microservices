package com.palavecinofranco.notification_service.events;

import com.palavecinofranco.notification_service.enums.OrderStatus;

public record OrderEvent(String orderNumber, int itemCount, OrderStatus orderStatus) {
}

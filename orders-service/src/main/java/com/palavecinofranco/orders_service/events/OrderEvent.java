package com.palavecinofranco.orders_service.events;

import com.palavecinofranco.orders_service.model.enums.OrderStatus;

public record OrderEvent(String orderNumber, int itemCount, OrderStatus orderStatus) {
}

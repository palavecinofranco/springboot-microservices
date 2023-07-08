package com.palavecinofranco.orders_service.model.dtos;

import java.math.BigDecimal;

public record OrderItemsResponse(Long id, String sku, Double price, Long quantity) {

}

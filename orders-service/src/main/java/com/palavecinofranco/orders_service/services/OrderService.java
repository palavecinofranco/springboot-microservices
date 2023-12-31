package com.palavecinofranco.orders_service.services;

import com.palavecinofranco.orders_service.model.enums.OrderStatus;
import com.palavecinofranco.orders_service.events.OrderEvent;
import com.palavecinofranco.orders_service.model.dtos.*;
import com.palavecinofranco.orders_service.model.entities.Order;
import com.palavecinofranco.orders_service.model.entities.OrderItem;
import com.palavecinofranco.orders_service.repositories.OrderRepository;
import com.palavecinofranco.orders_service.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClient;
    private final KafkaTemplate<String,String> kafkaTemplate;

    public OrderResponse placeOrder(OrderRequest orderRequest){
        // TODO --- Check for inventory
        BaseResponse result = this.webClient.build()
                .post()
                .uri("lb://inventory-service/api/inventory/in-stock")
                .bodyValue(orderRequest.getOrderItems())
                .retrieve()
                .bodyToMono(BaseResponse.class)
                .block();

        if(result!=null && result.hasErrors()) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setOrderItems(orderRequest.getOrderItems().stream()
                    .map(orderItemRequest -> mapOrderItemRequestToOrderItem(orderItemRequest, order))
                    .toList());
            var savedOrder = this.orderRepository.save(order);

            //TODO --- Send message to order topic
            this.kafkaTemplate.send("orders-topic", JsonUtils.toJson(
                    new OrderEvent(savedOrder.getOrderNumber(), savedOrder.getOrderItems().size(), OrderStatus.PLACED)
            ));
            return mapToOrderResponse(savedOrder);
        } else{
            throw new IllegalArgumentException("Some of the products are not in stock");
        }
    }

    public List<OrderResponse> getAllOrders(){
        List<Order> orders = this.orderRepository.findAll();
        return orders.stream().map(this::mapToOrderResponse).toList();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(order.getId(), order.getOrderNumber(), order.getOrderItems().stream().map(this::mapToOrderItemRequest).toList());
    }

    private OrderItemsResponse mapToOrderItemRequest(OrderItem orderItem) {
        return new OrderItemsResponse(orderItem.getId(), orderItem.getSku(), orderItem.getPrice(), orderItem.getQuantity());
    }

    private OrderItem mapOrderItemRequestToOrderItem(OrderItemRequest orderItemRequest, Order order) {
        return OrderItem.builder()
                .id(orderItemRequest.getId())
                .sku(orderItemRequest.getSku())
                .price(orderItemRequest.getPrice())
                .quantity(orderItemRequest.getQuantity())
                .order(order)
                .build();
    }
}
